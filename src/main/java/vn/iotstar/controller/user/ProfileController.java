package vn.iotstar.controller.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Person;
import vn.iotstar.entity.User;
import vn.iotstar.service.IUserService;

@Controller
@RequestMapping("/User")
public class ProfileController {
	@Autowired
	private IUserService userService;
	@PostMapping("/editProfile")
	public String editProfile(ModelMap model, HttpSession session, @ModelAttribute("user") User user ) {
		
		Optional<Person> opUser = userService.findById(user.getId());
		
		if (opUser.isPresent())
		{
			Person updateuser = opUser.get();
			updateuser.setFullname(user.getFullname());
			updateuser.setEmail(user.getEmail());
			updateuser.setPhone(user.getPhone());
			updateuser.setGender(user.getGender());
			userService.save(updateuser);
			return "redirect:/User/dashboard";
		}
		else 
		{
			model.addAttribute("ErrUpdateProfile","Chỉnh sửa thông tin cá nhân không thành công.");
			return "redirect:/User/dashboard";
		}
		
	}
}
