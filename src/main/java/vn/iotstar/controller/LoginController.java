package vn.iotstar.controller;

import java.util.regex.Pattern;

import org.slf4j.Logger; // Thêm import
import org.slf4j.LoggerFactory; // Thêm import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException; // Thêm import
import org.springframework.security.authentication.LockedException; // Thêm import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; // Thêm Model
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Account;
import vn.iotstar.entity.Person;
import vn.iotstar.service.IAccountService;
import vn.iotstar.service.IEmailService;
import vn.iotstar.service.IEmailService;
import vn.iotstar.service.IPersonService;
import vn.iotstar.service.JwtService;
import vn.iotstar.service.impl.UserService; // Giả sử bạn có một service gửi email

@Controller
public class LoginController {

    // Khởi tạo Logger
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private IAccountService accountSer;
    @Autowired
    private IPersonService personSer;
    @Autowired
    private IEmailService emailService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    // @Autowired
    // private UserService userservice; // Bỏ nếu không dùng

    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(value = "success", required = false) String success,
                                @RequestParam(value = "error", required = false) String error) {
        if (success != null) {
            model.addAttribute("successMessage", "Đổi mật khẩu thành công. Vui lòng đăng nhập lại.");
        }
        if (error != null) {
            model.addAttribute("loginError", "Có lỗi xảy ra hoặc phiên làm việc đã hết hạn. Vui lòng thử lại.");
        }
        return "Guest/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam(value = "remember-me", required = false) String rememberMe,
                              HttpServletRequest request, HttpServletResponse response, Model model) { // Thêm Model
        HttpSession session = request.getSession();
        String ipAddress = request.getRemoteAddr();

        Integer loginAttempts = (Integer) session.getAttribute("LOGIN_ATTEMPTS");
        if (loginAttempts == null) {
            loginAttempts = 0;
        }

        Long lockTime = (Long) session.getAttribute("LOCK_TIME");
        if (loginAttempts >= 3) {
            if (lockTime != null && System.currentTimeMillis() < lockTime) {
                long remainingLockTimeMinutes = Math.max(0, (lockTime - System.currentTimeMillis()) / (1000 * 60));
                String lockMessage = "Tài khoản hoặc IP của bạn đã bị khóa tạm thời do đăng nhập sai nhiều lần. Vui lòng thử lại sau " + remainingLockTimeMinutes + " phút.";
                logger.warn("Login attempt from IP '{}' for user '{}' while account/IP is locked. Remaining time: {} minutes.", ipAddress, username, remainingLockTimeMinutes);
                model.addAttribute("loginError", lockMessage);
                return "Guest/login";
            } else {
                loginAttempts = 0;
                session.setAttribute("LOGIN_ATTEMPTS", loginAttempts);
                session.removeAttribute("LOCK_TIME");
                logger.info("Lock expired for IP '{}' or user '{}'. Login attempts reset.", ipAddress, username);
            }
        }

        // Kiểm tra độ dài username trước khi xác thực (GIỚI HẠN CỦA DB LÀ 255)
        if (username != null && username.length() > 255) {
            loginAttempts++;
            session.setAttribute("LOGIN_ATTEMPTS", loginAttempts);
            String errorMsg = "Tên đăng nhập không hợp lệ (quá dài).";
            logger.warn("Login attempt FAILED for IP '{}': Username too long (length: {}). Attempt #{}.", ipAddress, username.length(), loginAttempts);
            
            if (loginAttempts >= 3) {
                session.setAttribute("LOCK_TIME", System.currentTimeMillis() + 60 * 60 * 1000); // 1 tiếng
                logger.warn("Account/IP locked for IP '{}' after {} failed attempts (username too long).", ipAddress, loginAttempts);
                errorMsg += " Tài khoản hoặc IP của bạn đã bị khóa tạm thời.";
            }
            model.addAttribute("loginError", errorMsg);
            return "Guest/login";
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            
            session.setAttribute("LOGIN_ATTEMPTS", 0);
            session.removeAttribute("LOCK_TIME");
            logger.info("Login SUCCEEDED for user '{}' from IP '{}'.", username, ipAddress);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = jwtService.generateToken(username);
            Cookie jwtCookie = new Cookie("JWT_TOKEN", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(request.isSecure()); 
            jwtCookie.setPath("/");
            int cookieMaxAge = "on".equals(rememberMe) ? 7 * 24 * 60 * 60 : 30 * 60; 
            jwtCookie.setMaxAge(cookieMaxAge);
            response.addCookie(jwtCookie);

            return "redirect:/waiting";

        } catch (BadCredentialsException e) {
            loginAttempts++;
            session.setAttribute("LOGIN_ATTEMPTS", loginAttempts);
            logger.warn("Login attempt FAILED for user '{}' from IP '{}'. Attempt #{}. Reason: Bad credentials.", username, ipAddress, loginAttempts);
            model.addAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không chính xác.");
        } catch (LockedException e) { 
            loginAttempts++; 
            session.setAttribute("LOGIN_ATTEMPTS", loginAttempts);
            logger.warn("Login attempt FAILED for user '{}' from IP '{}'. Attempt #{}. Reason: Account locked (Spring Security). Message: {}", username, ipAddress, loginAttempts, e.getMessage());
            model.addAttribute("loginError", "Tài khoản này đã bị khóa: " + e.getMessage());
        }
        catch (AuthenticationException e) { // Các lỗi xác thực khác từ Spring Security
            loginAttempts++;
            session.setAttribute("LOGIN_ATTEMPTS", loginAttempts);
            logger.warn("Login attempt FAILED for user '{}' from IP '{}'. Attempt #{}. Reason: {}", username, ipAddress, loginAttempts, e.getMessage());
            model.addAttribute("loginError", "Lỗi xác thực: " + e.getMessage());
        } catch (Exception e) { // Các lỗi không mong muốn khác (DB, NullPointer,...)
            loginAttempts++;
            session.setAttribute("LOGIN_ATTEMPTS", loginAttempts);
            logger.error("Unexpected error during login attempt for user '{}' from IP '{}'. Attempt #{}. Exception: ", username, ipAddress, loginAttempts, e);
            model.addAttribute("loginError", "Đã có lỗi không mong muốn xảy ra trong quá trình đăng nhập. Vui lòng thử lại.");
        }

        if (loginAttempts >= 3 && session.getAttribute("LOCK_TIME") == null) { 
            session.setAttribute("LOCK_TIME", System.currentTimeMillis() + 60 * 60 * 1000); 
            logger.warn("Account/IP locked for user '{}' or IP '{}' after {} failed attempts.", username, ipAddress, loginAttempts);
            // Lấy thông báo lỗi đã có (nếu có) và thêm thông báo khóa
            String currentError = (String) model.getAttribute("loginError");
            String lockMessage = " Tài khoản hoặc IP của bạn đã bị khóa tạm thời do đăng nhập sai nhiều lần.";
            model.addAttribute("loginError", (currentError != null ? currentError : "") + lockMessage);
        }
        return "Guest/login"; 
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "Guest/forgot-password";
    }

    @PostMapping("/process-forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("Forgot password request initiated for email '{}' from IP '{}'.", email, ipAddress);
        Person person = personSer.findByEmail(email);

        if (person != null) {
            Account account = person.getAccount();
            if (account != null) {
                String otp = generateOtp();
                account.setToken(otp);
                // NÊN: Lưu thời gian tạo OTP hoặc thời gian hết hạn OTP vào Account Entity
                // account.setOtpCreationTime(System.currentTimeMillis()); 
                accountSer.update(account); 

                try {
                    emailService.sendOtp(email, otp);
                    logger.info("OTP sent to email '{}' for forgot password request from IP '{}'.", email, ipAddress);
                    model.addAttribute("email", email); 
                    return "Guest/verify-otp";
                } catch (Exception e) {
                    logger.error("Failed to send OTP email to '{}' from IP '{}'. Exception: ", email, ipAddress, e);
                    model.addAttribute("error", "Không thể gửi email OTP. Vui lòng thử lại hoặc liên hệ quản trị viên.");
                    return "Guest/forgot-password";
                }
            } else {
                logger.warn("Forgot password attempt for email '{}' from IP '{}': No account linked to this email.", email, ipAddress);
                model.addAttribute("error", "Không tìm thấy tài khoản liên kết với email này.");
                return "Guest/forgot-password";
            }
        } else {
            logger.warn("Forgot password attempt for email '{}' from IP '{}': Email does not exist.", email, ipAddress);
            model.addAttribute("error", "Email không tồn tại trong hệ thống!");
            return "Guest/forgot-password";
        }
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam("otp") String otp, @RequestParam("email") String email, Model model, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("OTP verification attempt for email '{}' with OTP '{}' from IP '{}'.", email, otp, ipAddress);
        model.addAttribute("email", email); // Giữ lại email cho form

        if (isOtpValid(otp, email)) { // isOtpValid cần kiểm tra thời gian hết hạn
            logger.info("OTP verification successful for email '{}' from IP '{}'.", email, ipAddress);
            // Không vô hiệu hóa OTP ở đây, mà là sau khi reset pass thành công
            model.addAttribute("otp_token", otp); // Truyền OTP đã xác thực
            return "Guest/process-reset-password";
        } else {
            logger.warn("OTP verification FAILED for email '{}' with OTP '{}' from IP '{}'. Invalid or expired OTP.", email, otp, ipAddress);
            model.addAttribute("error", "Mã OTP không chính xác hoặc đã hết hạn!");
            return "Guest/verify-otp";
        }
    }

    @PostMapping("/process-reset-password")
    public String processResetPassword(@RequestParam("otp_token") String otpToken,
                                      @RequestParam("email") String email, 
                                      @RequestParam("newPassword") String newPassword,
                                      @RequestParam("confirmPassword") String confirmPassword,
                                      Model model, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        logger.info("Password reset attempt for email '{}' using OTP token from IP '{}'.", email, ipAddress);
        model.addAttribute("email", email); // Giữ lại cho form
        model.addAttribute("otp_token", otpToken); // Giữ lại cho form

        if (!newPassword.equals(confirmPassword)) {
            logger.warn("Password reset attempt FAILED for email '{}' from IP '{}': Passwords do not match.", email, ipAddress);
            model.addAttribute("error", "Mật khẩu xác nhận không khớp với mật khẩu mới!");
            return "Guest/process-reset-password";
        }
        
        String passwordValidationError = validatePassword(newPassword);
        if (passwordValidationError != null) {
            logger.warn("Password reset attempt FAILED for email '{}' from IP '{}': Weak password. Reason: {}", email, ipAddress, passwordValidationError);
            model.addAttribute("error", passwordValidationError);
            return "Guest/process-reset-password";
        }

        Person person = personSer.findByEmail(email);
        if (person == null || person.getAccount() == null) {
            logger.error("Password reset attempt FAILED for email '{}' from IP '{}': Account not found for email during reset.", email, ipAddress);
            model.addAttribute("error", "Không tìm thấy tài khoản hoặc có lỗi xảy ra. Vui lòng thử lại quy trình quên mật khẩu.");
            return "redirect:/forgot-password?error=account_not_found"; 
        }
        Account acc = person.getAccount();
        
        // Kiểm tra lại xem token trong DB có khớp với otpToken đã được xác thực không
        // Và token này phải còn hiệu lực (chưa hết hạn, chưa được dùng)
        if (acc.getToken() == null || !acc.getToken().equals(otpToken) /* || isOtpActuallyExpired(acc) */ ) {
            logger.warn("Password reset attempt FAILED for email '{}' from IP '{}': Invalid, used, or expired OTP token '{}' for reset.", email, ipAddress, otpToken);
            model.addAttribute("error", "Mã xác thực không hợp lệ, đã được sử dụng hoặc đã hết hạn. Vui lòng bắt đầu lại quy trình quên mật khẩu.");
            return "redirect:/forgot-password?error=invalid_token"; 
        }

        // Mã hóa mật khẩu mới trước khi lưu
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        
        // Giả sử accountSer.resetPassword nhận token (để tìm user) và mật khẩu đã mã hóa
        // Hoặc tốt hơn: accountSer.resetPasswordForAccount(Account account, String encodedNewPassword)
        if (accountSer.resetPassword(otpToken, encodedNewPassword)) { // Cần xem lại phương thức này trong IAccountService
            // Vô hiệu hóa token sau khi reset thành công
            acc.setToken(null); 
            // acc.setOtpCreationTime(null); // Reset thời gian tạo/hết hạn
            accountSer.update(acc); // Lưu lại việc vô hiệu hóa token
            
            logger.info("Password reset SUCCEEDED for email '{}' (account_id: {}) from IP '{}'.", email, acc.getAccountId(), ipAddress);
            return "redirect:/login?success=reset"; // Redirect với param để trang login biết mà hiển thị thông báo
        } else {
            logger.error("Password reset FAILED for email '{}' (account_id: {}) from IP '{}' during database update for resetPassword method.", email, acc.getAccountId(), ipAddress);
            model.addAttribute("error", "Có lỗi xảy ra khi đặt lại mật khẩu! Vui lòng thử lại.");
            return "Guest/process-reset-password";
        }
    }

    private String generateOtp() {
        int otp = 100000 + (int) (Math.random() * 900000);
        return String.valueOf(otp);
    }

    private boolean isOtpValid(String otp, String email) {
        Person person = personSer.findByEmail(email);
        if (person == null) {
            logger.debug("isOtpValid: Person not found for email '{}'", email);
            return false;
        }
        Account account = person.getAccount();

        if (account != null && account.getToken() != null && account.getToken().equals(otp)) {
            // NÊN: Thêm kiểm tra thời gian hết hạn của OTP
            // if (account.getOtpCreationTime() != null && (System.currentTimeMillis() - account.getOtpCreationTime()) > (10 * 60 * 1000)) { // Hết hạn sau 10 phút
            //    logger.warn("OTP for email '{}' has expired. OTP was '{}'.", email, otp);
            //    return false; 
            // }
            return true;
        }
        if (account == null) logger.debug("isOtpValid: Account not found for person with email '{}'", email);
        else if (account.getToken() == null) logger.debug("isOtpValid: No token stored for account with email '{}'", email);
        else logger.debug("isOtpValid: Stored token '{}' does not match provided OTP '{}' for email '{}'", account.getToken(), otp, email);
        return false;
    }
    
    private String validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return "Mật khẩu không được để trống!";
        }
        if (password.length() < 8) {
            return "Mật khẩu phải có ít nhất 8 ký tự!";
        }
        if (password.contains(" ")) {
            return "Mật khẩu không được chứa khoảng trắng!";
        }
        String upperCaseRegex = ".*[A-Z].*";
        String lowerCaseRegex = ".*[a-z].*";
        String digitRegex = ".*\\d.*";
        String specialCharRegex = ".*[!@#$%^&*(),.?\":{}|<>].*";
        if (!Pattern.matches(upperCaseRegex, password)) {
            return "Mật khẩu phải chứa ít nhất 1 chữ cái in hoa!";
        }
        if (!Pattern.matches(lowerCaseRegex, password)) {
            return "Mật khẩu phải chứa ít nhất 1 chữ cái thường!";
        }
        if (!Pattern.matches(digitRegex, password)) {
            return "Mật khẩu phải chứa ít nhất 1 số!";
        }
        if (!Pattern.matches(specialCharRegex, password)) {
            return "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt (ví dụ: !@#$%)!";
        }
        return null;
    }
}