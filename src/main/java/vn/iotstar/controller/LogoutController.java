package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class LogoutController {

//	@GetMapping("/logout")
//	public String logout(HttpSession session, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
//	    // Xóa session
//	    if (session != null) {
//	        session.invalidate(); // Hủy session hiện tại
//	    }
//
//	    // Xóa cookie "rememberMe"
//	    Cookie[] cookies = request.getCookies();
//	    if (cookies != null) {
//	        for (Cookie cookie : cookies) {
//	            if ("rememberMe".equals(cookie.getName())) {
//	                cookie.setValue(null);
//	                cookie.setMaxAge(0); // Đặt thời gian sống của cookie về 0 để xóa cookie
//	                cookie.setPath("/"); // Đảm bảo cookie có path đúng
//	                response.addCookie(cookie); // Thêm lại cookie đã xóa vào response
//	            }
//	        }
//	    }
//
//	    // Thêm thông báo đăng xuất thành công vào flash attribute
//	    redirectAttributes.addFlashAttribute("message", "Bạn đã đăng xuất thành công");
//
//	    // Chuyển hướng về trang home (hoặc login nếu bạn muốn)
//	    return "redirect:/"; // Hoặc trả về "redirect:/login" nếu bạn muốn chuyển đến trang đăng nhập
//	}

}
