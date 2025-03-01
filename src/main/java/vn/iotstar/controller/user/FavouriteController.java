package vn.iotstar.controller.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Favourite;
import vn.iotstar.entity.Person;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Vendor;
import vn.iotstar.service.IFavouriteService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IUserService;

@Controller
@RequestMapping("/User")
public class FavouriteController {
	@Autowired
	private IFavouriteService favouriteService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IProductService productService;
	
	@PostMapping("/addlove")
	public ResponseEntity<String> addlove(ModelMap model, HttpSession session, @RequestParam int productId)
	{
		Person user = (Person) session.getAttribute("user");
        
        int customerId=0;
        List<Favourite> listfavou = new ArrayList<>();
        if (user.getAccount().getRole().getRoleId() == 2)
        {
        	User u = (User) user;
        	listfavou = u.getFavourite();
        	if (listfavou == null)
    		{
    			listfavou = new ArrayList<>();
    			u.setFavourite(listfavou);
    			userService.save(u);
    		}
        }
        else if (user.getAccount().getRole().getRoleId() == 3) {
        	Vendor u = (Vendor) user;
        	listfavou = u.getFavourite();
        	if (listfavou == null)
    		{
    			listfavou = new ArrayList<>();
    			u.setFavourite(listfavou);
    			userService.save(u);
    		}
        }

		Optional<Favourite> favourite = favouriteService.findByUserId(user.getId(),productId);
		
		if (!favourite.isPresent())
		{
			Favourite newfavou = new Favourite();
			newfavou.setProduct(productService.findById(productId).get());
			newfavou.setUser(user);
			favouriteService.save(newfavou);
		}
		
		return ResponseEntity.ok("Thêm yêu thích thành công");
	}
	@PostMapping("/removelove")
	public ResponseEntity<String> removelove(ModelMap model, HttpSession session, @RequestParam int productId)
	{
		Person user = (Person) session.getAttribute("user");
        

		Optional<Favourite> favourite = favouriteService.findByUserId(user.getId(),productId);
		System.out.println(favourite.get().getProduct().getProductName());
		if (favourite.isPresent())
		{
			Favourite favou = favourite.get();
			favouriteService.deleteById(favou.getFavouriteId().intValue());
			return ResponseEntity.ok("Đã bỏ yêu thích");
		}
		
		return ResponseEntity.ok("Có lỗi");
	}
}
