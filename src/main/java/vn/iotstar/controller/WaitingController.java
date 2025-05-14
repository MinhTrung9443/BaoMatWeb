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
		// Get authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName() == null) {
            redirectAttributes.addFlashAttribute("error", "Please log in to continue.");
            return new ModelAndView("redirect:/login", model);
        }

        String username = authentication.getName();
        System.out.println("In waiting: " + username);

        // Fetch account details
        Account account = accountService.findByUsername(username);
        if (account == null || account.getRole() == null) {
            redirectAttributes.addFlashAttribute("error", "Account not found or invalid role.");
            return new ModelAndView("redirect:/login", model);
        }

        // Optionally store account in session (avoid if using JWT for stateless auth)
        session.setAttribute("account", account);

        // Redirect based on role
        int roleId = account.getRole().getRoleId();
        switch (roleId) {
            case 1:
                return new ModelAndView("redirect:/Admin", model);
            case 2:
                return new ModelAndView("redirect:/User", model);
            case 3:
                return new ModelAndView("redirect:/Vendor", model);
            case 4:
                return new ModelAndView("redirect:/Shipper", model);
            default:
                redirectAttributes.addFlashAttribute("error", "Invalid role.");
                return new ModelAndView("redirect:/", model);
        }
    }
}
