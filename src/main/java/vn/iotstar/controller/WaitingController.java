package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Account;
import vn.iotstar.service.IAccountService;

@Controller
public class WaitingController {
	@Autowired
	private IAccountService accountService;
	@GetMapping("/waiting")
	public ModelAndView waiting(ModelMap model, HttpSession session, RedirectAttributes redirectAttributes) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName(); // Lấy username
		System.out.println("dang trong waiting: " + username);
		
		Account account = accountService.findByUsername(username);
		session.setAttribute("account", account);
		int roleId = account.getRole().getRoleId();

		if (roleId == 1) {
			return new ModelAndView("redirect:/Admin", model);

		} else if (roleId == 2) {

			return new ModelAndView("redirect:/User", model);

		} else if (roleId == 3) {
			return new ModelAndView("redirect:/Vendor", model);
		} else if (roleId == 4) {
			return new ModelAndView("redirect:/Shipper", model);
		} else {
			return new ModelAndView("redirect:/", model);
		}
	}
}
