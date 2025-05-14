package vn.iotstar.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.mail.Session;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.dto.UserRegistrationDTO;
import vn.iotstar.entity.Account;
import vn.iotstar.entity.Address;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.service.IAccountService;
import vn.iotstar.service.IAddressService;
import vn.iotstar.service.IEmailService;
import vn.iotstar.service.IPersonService;
import vn.iotstar.service.IRoleService;
import vn.iotstar.service.IUserService;

@Controller
public class RegisterController {
	@Autowired
	private IAccountService accountService;

	@Autowired
	private IUserService userService;
	@Autowired
	private IPersonService personService;
	@Autowired
	private IAddressService addressService;
	@Autowired
	private IRoleService roleService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private IEmailService emailService; 

	// Hiển thị trang đăng kí
	@GetMapping("/register")
	public String showRegisterPage() {
		return "Guest/register"; // Trả về tên file `register.html` trong thư mục templates
	}

	@PostMapping("/process-register")
	public String processRegister(@ModelAttribute UserRegistrationDTO userDTO, Model model,HttpSession session) {
		boolean check = false;
		
		// Kiểm tra độ mạnh của mật khẩu
	    String password = userDTO.getPassword();
	    String passwordError = validatePassword(password);
	    if (passwordError != null) {
	        model.addAttribute("errorPassword", passwordError);
	        return "Guest/register";
	    }
		
		if(accountService.findByUsername(userDTO.getUsername())!= null)
		{
			model.addAttribute("errorUsername", "Tên đăng nhập đã tồn tại!");
			return "Guest/register"; 
		}
		if (personService.findByEmail(userDTO.getEmail())!= null) {
			model.addAttribute("errorMail", "Email đã tồn tại!");
			return "Guest/register"; 
		}
		if (personService.findByPhone(userDTO.getPhone()) != null)
		{
			model.addAttribute("errorPhone", "Số điện thoại đã tồn tại!");
			return "Guest/register"; 
		}
		String phoneRegex = "^\\d{10}$";
		if (!Pattern.matches(phoneRegex, userDTO.getPhone())) {
			model.addAttribute("errorPhone", "Số điện thoại không hợp lệ");
			return "Guest/register"; 
		}
		Address add = new Address();
		add.setAddressDetail(userDTO.getAddress_detail());
		add.setAddressType(userDTO.getAddress_type());
		Account account = new Account();
		account.setUsername(userDTO.getUsername());
		account.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		String otp = generateOtp();
		account.setToken(otp);
		Role role = roleService.findByName("USER"); 
		account.setRole(role);
		account.setActive(0);
		accountService.save(account);
		// Gửi OTP qua email
		System.out.println(userDTO.getUsername());
		emailService.sendRegisterOtp(userDTO.getEmail(), otp);
		model.addAttribute("username",userDTO.getUsername());
		model.addAttribute("email", userDTO.getEmail()); // Truyền email vào model
		model.addAttribute("fullname",userDTO.getFullname());
		model.addAttribute("phone",userDTO.getPhone());
		session.setAttribute("add", add);
		return "Guest/register-verify-otp"; // Chuyển đến trang xác minh OTP
		
	}
		// Xử lý form xác minh OTP
		@PostMapping("/register-verify-otp")
		public String processForgotPassword(HttpSession session,@RequestParam("email") String email,@RequestParam("otp") String otp,@RequestParam("username") String username,@RequestParam("fullname") String fullname,@RequestParam("phone") String phone, @ModelAttribute UserRegistrationDTO userDTO , Model model) {
			Account account = accountService.findByUsername(username);

			try {
			if (isOtpValid(otp, account)) {
			account.setActive(1);
			account.setToken(null);
			accountService.update(account); 
			User user = new User();
			user.setFullname(fullname);
			user.setEmail(userDTO.getEmail());
			user.setPhone(phone);
			if ("Male".equals(userDTO.getGender())) {
				user.setGender(1); 
			} else if ("Female".equals(userDTO.getGender())) {
				user.setGender(0); 
			}
			
			user.setBirthday(userDTO.getBirthday());
			user.setAccount(account); 
			Address add = (Address)session.getAttribute("add");
			List<Address> list = new ArrayList<>();
			list.add(add);
			user.setAddress(list);
			userService.save(user); 
			

			if (userDTO.getAddress_detail() != null && !userDTO.getAddress_type().isEmpty()) {
				Address address = new Address();
				address.setAddressDetail(userDTO.getAddress_detail());
				address.setAddressType(userDTO.getAddress_type());
				address.setUser(user); 
				addressService.save(address); 
			}
	 
			model.addAttribute("user", userDTO);// Truyền account vào model
			model.addAttribute("email", userDTO.getEmail()); // Truyền email vào model
			model.addAttribute("successMessage", "Đăng ký thành công!");
			return "redirect:/login"; 
			}
			else
			{
				model.addAttribute("error", "Mã OTP không chính xác!");
				return "Guest/register-verify-otp"; // Quay lại form xác minh OTP nếu sai
			}
			
		} catch (Exception e) {
			e.printStackTrace(); 

			model.addAttribute("error", "Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại!");
			return "Guest/register"; 
		}
	}

	private String generateOtp() {
		int otp = 100000 + (int) (Math.random() * 900000); // Sinh mã OTP 6 chữ số
		return String.valueOf(otp);
	}
	private boolean isOtpValid(String otp, Account account) {
		

		// Kiểm tra nếu tài khoản tồn tại và mã OTP khớp với token đã lưu trong cơ sở dữ
		// liệu
		if (account.getToken() != null && account.getToken().equals(otp)) {
			// Kiểm tra nếu OTP chưa hết hạn (nếu bạn lưu thời gian hết hạn OTP)
			// Nếu có thêm thuộc tính otpExpirationTime trong Account, bạn có thể kiểm tra
			// thêm

			return true; // OTP hợp lệ

		}
		return false; // OTP không hợp lệ hoặc hết hạn
	}
	
	// Hàm kiểm tra độ mạnh mật khẩu
	private String validatePassword(String password) {
	    // Kiểm tra null hoặc rỗng
	    if (password == null || password.trim().isEmpty()) {
	        return "Mật khẩu không được để trống!";
	    }

	    // Kiểm tra độ dài tối thiểu
	    if (password.length() < 8) {
	        return "Mật khẩu phải có ít nhất 8 ký tự!";
	    }

	    // Kiểm tra khoảng trắng
	    if (password.contains(" ")) {
	        return "Mật khẩu không được chứa khoảng trắng!";
	    }

	    // Kiểm tra các loại ký tự
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

	    return null; // Mật khẩu hợp lệ
	}

}
