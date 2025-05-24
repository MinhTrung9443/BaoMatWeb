package vn.iotstar.controller.user;

import java.nio.charset.StandardCharsets;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.*;
import vn.iotstar.enums.OrderStatus;
import vn.iotstar.repository.IOrderRepository;
import vn.iotstar.service.*;

@Controller
@RequestMapping("/User")
public class UserController {
	@Autowired
	private IProductService productService;
	@Autowired
	private IUserService userService;
	@Autowired
	private ICartService cartService;
	@Autowired
	private IFavouriteService favouriteService;
	@Autowired
	private IViewHistoryService viewService;
	@Autowired
	private ICategoryService categoryService;
	@Autowired
	private IVoucherService voucherService;


	@GetMapping("")
	public String home(HttpSession session, ModelMap model) {
		Account account = (Account) session.getAttribute("account");
		int roleid = account.getRole().getRoleId();
		if (roleid == 2) {
			User customer = (User) userService.findByAccountUsername(account.getUsername());
			session.setAttribute("user", customer);
		} else if (roleid == 3) {
			Vendor employee = (Vendor) userService.findByAccountUsername(account.getUsername());
			session.setAttribute("user", employee);
		}

		List<Product> productNew = productService.findTop20ByOrderByWarehouseDateFirstDesc();

		  List<Product> productSale = productService.findTop20BySalesQuantity();

		  Page<Product> productRate = productService.findTop20ByAverageRating();

		  List<Product> productFavou = productService.findTop20ByFavouriteCount();
		  List<Category> listCate = categoryService.findAll();
	        session.setAttribute("listCate", listCate);
		model.addAttribute("productNew", productNew);
		  model.addAttribute("productSale",productSale);
		  model.addAttribute("productRate",productRate);
		  model.addAttribute("productFavou",productFavou);

		return "User/index";
	}

	@GetMapping("/cart")
	public String cart(HttpSession session, ModelMap model) {
		Account account = (Account) session.getAttribute("account");
		int roleid = account.getRole().getRoleId();
		int id=0;
		if (roleid == 2) {
			User customer = (User) userService.findByAccountUsername(account.getUsername());
			id = customer.getId();
			session.setAttribute("user", customer);
		} else if (roleid == 3) {
			Vendor employee = (Vendor) userService.findByAccountUsername(account.getUsername());
			id = employee.getId();
			session.setAttribute("user", employee);
		}
		
		Optional<ShoppingCart> cart = cartService.findByUserId(id);
		ShoppingCart scart = new ShoppingCart();
		if (!cart.isPresent()) {
			scart = new ShoppingCart();
			List<CartItem> cartItems = new ArrayList<>();
			scart.setItems(cartItems);
		}
		else 
		{
			scart = cart.get();
		}
		model.addAttribute("listCart", scart.getItems());
		return "User/cart";
	}

	@GetMapping({ "/wishlist" })
	public String wishlist(HttpSession session, ModelMap model,
			@RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {
		int pageSize = 5;
		if (pageNo <= 0) {
			pageNo = 1;
		}
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Order.desc("favouriteId")));

		Person person = (Person) session.getAttribute("user");
        
        int Id=0;
        if (person.getAccount().getRole().getRoleId() == 2)
        {
        	User u = (User) person;
        	Id = u.getId();
        }
        else if (person.getAccount().getRole().getRoleId() == 3) {
        	Vendor u = (Vendor) person;
        	Id = u.getId();
        }

		Page<Favourite> listfavou = favouriteService.findAllByUserId(Id,pageable);

		model.addAttribute("listProduct", listfavou);
		model.addAttribute("currentPage", pageNo);
	    model.addAttribute("pageSize", pageSize);
	    model.addAttribute("totalItems", favouriteService.countAllByUserId(Id));
	    model.addAttribute("totalPages", (int) Math.ceil((double) favouriteService.countAllByUserId(Id) / pageSize));
		return "User/wishlist";
	}
	
	@GetMapping({ "/ViewHistory" })
	public String ViewHistory(HttpSession session, ModelMap model,
			@RequestParam(value = "pageNo", defaultValue = "1") int pageNo) {
		int pageSize = 5;
		if (pageNo <= 0) {
			pageNo = 1;
		}
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Order.desc("viewHistoryId")));

		Person person = (Person) session.getAttribute("user");
        
        int Id=0;
        if (person.getAccount().getRole().getRoleId() == 2)
        {
        	User u = (User) person;
        	Id = u.getId();
        }
        else if (person.getAccount().getRole().getRoleId() == 3) {
        	Vendor u = (Vendor) person;
        	Id = u.getId();
        }

		Page<ViewHistory> listView = viewService.findAllByUserId(Id,pageable);

		model.addAttribute("listProduct", listView);
		model.addAttribute("currentPage", pageNo);
	    model.addAttribute("pageSize", pageSize);
	    model.addAttribute("totalItems", viewService.countAllByUserId(Id));
	    model.addAttribute("totalPages", (int) Math.ceil((double) viewService.countAllByUserId(Id) / pageSize));
		return "User/ViewHistory";
	}
	@Autowired
	IOrderRepository repo;
	
	@GetMapping("/dashboard")
	public String dashboard(HttpSession session, ModelMap model) {
		String userEmail = null;
		Account account = (Account) session.getAttribute("account");
		int roleid = account.getRole().getRoleId();
		if (roleid == 2) {
			User customer = (User) userService.findByAccountUsername(account.getUsername());
			session.setAttribute("user", customer);
			model.addAttribute("user", customer);
			userEmail = customer.getEmail();
		} else if (roleid == 3) {
			Vendor employee = (Vendor) userService.findByAccountUsername(account.getUsername());
			session.setAttribute("user", employee);
			model.addAttribute("user", employee);
			userEmail = employee.getEmail();
		}
		// Mã hóa email nếu có
		if (userEmail != null && !userEmail.isEmpty()) {
			String encodedEmail = Base64.getEncoder().encodeToString(userEmail.getBytes(StandardCharsets.UTF_8));
			model.addAttribute("encodedUserEmail", encodedEmail);
		} else {
			model.addAttribute("encodedUserEmail", ""); // Gửi chuỗi rỗng nếu không có email
		}

		
		return "User/dashboard";
	}

	@Autowired
	private IProductFeedbackService feedbackService;
	@GetMapping("/productDetail/{productId}")
	public String productDetail(HttpSession session, ModelMap model, @PathVariable("productId") int productId) {
		Person person = (Person) session.getAttribute("user");
        
        int Id=0;
        if (person.getAccount().getRole().getRoleId() == 2)
        {
        	User u = (User) person;
        	Id = u.getId();
        }
        else if (person.getAccount().getRole().getRoleId() == 3) {
        	Vendor u = (Vendor) person;
        	Id = u.getId();
        }
		Optional<Product> opProduct = productService.findById(productId);

		if (opProduct.isPresent()) {
			Product product = opProduct.get();
			List<ProductFeedback> feedback = feedbackService.findByProduct_ProductId(productId);
			Double rating = feedbackService.findAverageRatingByProductId((long) productId);
			double finalRating = (rating != null) ? rating : 0.0;
			Integer review = product.getFeedbacks().size();
			int numReview = (review != null) ? review : 0;
			model.addAttribute("numReview", numReview);
			model.addAttribute("rating", finalRating);
			model.addAttribute("product", product);
			model.addAttribute("feedback", feedback);

			ViewHistory view = viewService.findByUserIdAndProduct_ProductId(Id, productId);
			if (view != null)
			{
				viewService.deleteById(view.getViewHistoryId().intValue());
			}
			ViewHistory newView = new ViewHistory();
			newView.setProduct(product);
			newView.setUser(person);
			viewService.save(newView);
			
			List<Product> top5 = productService.findTop5ByFavouriteCount(product.getCategory().getCategoryId());
			Map<Product, Double> top5Product = new HashMap<>();
			for (Product pro : top5)
			{
				Double rate = feedbackService.findAverageRatingByProductId((long) productId);
				double rate2 = (rate != null) ? rating : 0.0;
				top5Product.put(pro, rate2);
			}
			model.addAttribute("top5",top5Product);
			
			Voucher voucher = voucherService.findRandomValidActiveVoucher();
			
			model.addAttribute("voucher", voucher);
			return "/User/product";
		}
		return "redirect/User";
	}
	
}
