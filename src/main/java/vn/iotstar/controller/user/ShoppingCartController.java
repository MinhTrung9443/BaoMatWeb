package vn.iotstar.controller.user;

import java.util.ArrayList;
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
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Person;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.ShoppingCart;
import vn.iotstar.entity.User;
import vn.iotstar.service.ICartItemService;
import vn.iotstar.service.ICartService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IUserService;

@Controller
@RequestMapping("/User")
public class ShoppingCartController {
	@Autowired
	private IProductService productService;
	@Autowired
	private ICartItemService cartItemService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICartService cartService;
	@PostMapping("/add-to-cart")
	public String returnCartView(ModelMap model, HttpSession session, @RequestParam int productId, @RequestParam int quantity)
	{
		Product product = productService.findById(productId).get();
		Person user = (Person) session.getAttribute("user");
		if (user == null)
		{
			return "redirect:/login";
		}
		Optional<ShoppingCart> shopcart = cartService.findByUserId(user.getId());
		ShoppingCart cart;
		if (!shopcart.isPresent())
		{
			cart = new ShoppingCart();
			cart.setUser(user);
			cart.setItems(new ArrayList<>());
			cartService.save(cart);
		}
		else {
			cart = shopcart.get();
		}
		
		Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId()==productId)
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }

        cartService.save(cart);

		return "redirect:/User/cart";
	}
	@PostMapping("/remove-item")
    public ResponseEntity<String> removeItemFromCart(@RequestParam int cartItemId) {
        Optional<CartItem> cartItemOptional = cartItemService.findById(cartItemId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItemService.deleteById(cartItem.getCartItemId());
            return ResponseEntity.ok("Xoá thành công");
        } else {
            return ResponseEntity.badRequest().body("K tìm thấy");
        }
    }
	@PostMapping("/update-quantity")
    public ResponseEntity<?> updateQuantity(@RequestParam int cartItemId, @RequestParam int quantity) {
        Optional<CartItem> cartItemOptional = cartItemService.findById(cartItemId);
        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(quantity);
            
            if (cartItem.getProduct().getStock() < quantity)
            {
            	return new ResponseEntity<>("Số lượng hàng không đủ.",HttpStatus.BAD_REQUEST);
            }
            
            cartItemService.save(cartItem);
           
            return ResponseEntity.ok("Cập nhật thành công");
        } else {
            return new ResponseEntity<>("Không tìm thấy",HttpStatus.BAD_REQUEST);
        }
    }
}