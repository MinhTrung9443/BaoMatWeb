package vn.iotstar.controller.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Account;
import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Delivery;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderLine;
import vn.iotstar.entity.Payment;
import vn.iotstar.entity.Person;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.ShoppingCart;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Vendor;
import vn.iotstar.entity.Voucher;
import vn.iotstar.enums.OrderStatus;
import vn.iotstar.repository.IDeliveryRepository;
import vn.iotstar.repository.IOrderRepository;
import vn.iotstar.repository.IVoucherRepository;
import vn.iotstar.service.ICartItemService;
import vn.iotstar.service.ICartService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IUserService;
import vn.iotstar.service.payment.VNPAYService;


@Controller
@RequestMapping("/User")
public class CheckOutController {
	@Autowired
	private ICartService cartService;
	@Autowired
	private VNPAYService vnpayService;
	@Autowired
	private IOrderRepository orderService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IDeliveryRepository deliveryService;
	@Autowired
	private IVoucherRepository voucherService;
	@Autowired
	private IProductService productService;

	
	@PostMapping("/checkout")
	public String checkout(ModelMap model, HttpSession session, @RequestParam("shoppingCartId") int shoppingCartId)
	{

		Account acc = (Account) session.getAttribute("account");
		int roleid = acc.getRole().getRoleId();
		int id = 0;
		if (roleid == 2) {
			User customer = (User) userService.findByAccountUsername(acc.getUsername());
			id = customer.getId();
			session.setAttribute("user", customer);
		} else if (roleid == 3) {
			Vendor employee = (Vendor) userService.findByAccountUsername(acc.getUsername());
			id = employee.getId();
			session.setAttribute("user", employee);
		}
		
		
		
		ShoppingCart cart = cartService.findByUserId(id).get();
		
		List<Delivery> listDeli = deliveryService.findAll();
		
		int totalPriceProduct = 0;
		for (CartItem item : cart.getItems())
		{
			totalPriceProduct += item.getProduct().getPrice() * item.getQuantity();
		}
		
		model.addAttribute("totalPrice", totalPriceProduct);
		model.addAttribute("cart", cart);
		model.addAttribute("listdeli", listDeli);		
		
		
		return "/User/checkout";
	}
	
	@GetMapping("/checkout/product/{productId}")
	public String checkoutSingleProduct(ModelMap model, HttpSession session, @PathVariable("productId") int productId)
	{

		try {
			Account acc = (Account) session.getAttribute("account");
			int roleid = acc.getRole().getRoleId();
			int id = 0;
			if (roleid == 2) {
				User customer = (User) userService.findByAccountUsername(acc.getUsername());
				id = customer.getId();
				session.setAttribute("user", customer);
			} else if (roleid == 3) {
				Vendor employee = (Vendor) userService.findByAccountUsername(acc.getUsername());
				id = employee.getId();
				session.setAttribute("user", employee);
			}
			
			Product product = productService.findById(productId).get();
			
			if (product.getStock() < 1)
			{
				return "redirect:/User/productDetail/" + productId;
			}
			
			List<Delivery> listDeli = deliveryService.findAll();
			
					
			model.addAttribute("product", product);
			model.addAttribute("listdeli", listDeli);		
					
			return "/User/checkoutSingleProduct";
		}
		catch (Exception e) {
			e.printStackTrace();// TODO: handle exception
			return "redirect:/User/productDetail/" + productId;
		}
	}
	
	
	@PostMapping("/checkout-by-COD")
	public  ResponseEntity<?> checkoutByCode(ModelMap model, 
			HttpSession session, 
			@RequestParam(required = false) Integer shoppingCartId, 
			@RequestParam(required = false) Integer deliveryId, 
			@RequestParam(required = false) Integer voucherId, 
			@RequestParam(required = false) Integer total,
			@RequestParam(required = false) String shippingAddress)
	{
		Person user = (Person) session.getAttribute("user");
		
		Order order = new Order();
		order.setShippingAddress(shippingAddress);
		order.setOrderDate(LocalDateTime.now());
		order.setLines(new ArrayList<>());
		ShoppingCart cart = cartService.findById(shoppingCartId).get();
		for (CartItem item : cart.getItems())
		{
			OrderLine line = new OrderLine();
			line.setOrder(order);
			line.setProduct(item.getProduct());
			line.setQuantity(item.getQuantity());
			order.getLines().add(line);
			
		}
	
		Delivery deli = deliveryService.findById(deliveryId).get();
		order.setDelivery(deli);
		order.setUser(user);
		
		Payment payment = new Payment();
		payment.setPaymentMethod("COD");
		payment.setTotal(total);
		
		order.setOrderStatus(OrderStatus.PENDING);
		order.setPayment(payment);
		if (voucherId != null)
		{
			Voucher voucher = voucherService.findById(voucherId).get();
			voucher.setActive(0);
			voucher.setOrder(order);
			cartService.deleteAllCartItem(user.getId());
			voucherService.save(voucher);
		}
		else 
		{
			cartService.deleteAllCartItem(user.getId());
			orderService.save(order);
		}
		return new ResponseEntity<>("/User/Order",HttpStatus.OK);
	}
	
	@PostMapping("/checkout-by-VNPay")
	public  ResponseEntity<?> checkoutByVNpay(ModelMap model,
			HttpServletRequest request,
			HttpSession session, 
			@RequestParam(required = false) Integer shoppingCartId, 
			@RequestParam(required = false) Integer deliveryId, 
			@RequestParam(required = false) Integer voucherId, 
			@RequestParam(required = false) Integer total,
			@RequestParam(required = false) String shippingAddress)
	{
		Person user = (Person) session.getAttribute("user");
		
		Order order = new Order();
		order.setShippingAddress(shippingAddress);
		LocalDateTime now = LocalDateTime.now().withNano((LocalDateTime.now().getNano() / 1000) * 1000);
		order.setOrderDate(now);
		order.setLines(new ArrayList<>());
		ShoppingCart cart = cartService.findById(shoppingCartId).get();
		for (CartItem item : cart.getItems())
		{
			OrderLine line = new OrderLine();
			line.setOrder(order);
			line.setProduct(item.getProduct());
			line.setQuantity(item.getQuantity());
			order.getLines().add(line);
			
		}
	
		Delivery deli = deliveryService.findById(deliveryId).get();
		order.setDelivery(deli);
		order.setUser(user);
		
		Payment payment = new Payment();
		payment.setPaymentMethod("VNPay");
		payment.setTotal(total);
		
		order.setOrderStatus(OrderStatus.PENDING);
		order.setPayment(payment);
		if (voucherId != null)
		{
			Voucher voucher = voucherService.findById(voucherId).get();
			voucher.setActive(0);
			voucher.setOrder(order);
			session.setAttribute("orderVoucher", voucher);
		}
		session.setAttribute("checkingOutOrder", order);
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnpayService.createOrder(request, total, "DonHang_"+order.getOrderDate(), baseUrl);

		return new ResponseEntity<>(vnpayUrl,HttpStatus.OK);
	}
	
	@PostMapping("/singleCheckout-by-COD")
	public  ResponseEntity<?> singleCheckoutByCode(ModelMap model, 
			HttpSession session, 
			@RequestParam(required = false) Integer productId, 
			@RequestParam(required = false) Integer deliveryId, 
			@RequestParam(required = false) Integer voucherId, 
			@RequestParam(required = false) String shippingAddress)
	{
		Person user = (Person) session.getAttribute("user");
		
		Order order = new Order();
		order.setShippingAddress(shippingAddress);
		order.setOrderDate(LocalDateTime.now());
		order.setLines(new ArrayList<>());

		Product product = productService.findById(productId).get();
		
		OrderLine line = new OrderLine();
		line.setOrder(order);
		line.setProduct(product);
		line.setQuantity(1);
		order.getLines().add(line);
	
		Delivery deli = deliveryService.findById(deliveryId).get();
		order.setDelivery(deli);
		order.setUser(user);
		
		Payment payment = new Payment();
		payment.setPaymentMethod("COD");
		payment.setTotal(product.getPrice());
		
		order.setOrderStatus(OrderStatus.PENDING);
		order.setPayment(payment);
		if (voucherId != null)
		{
			Voucher voucher = voucherService.findById(voucherId).get();
			voucher.setActive(0);
			voucher.setOrder(order);
			voucherService.save(voucher);
		}
		else 
		{
			orderService.save(order);
		}
		productService.decreaseProductStock(productId);
		return new ResponseEntity<>("/User/Order",HttpStatus.OK);
	}
	
	@PostMapping("/singleCheckout-by-VNPay")
	public  ResponseEntity<?> singleCheckoutByVNpay(ModelMap model,
			HttpServletRequest request,
			HttpSession session, 
			@RequestParam(required = false) Integer productId, 
			@RequestParam(required = false) Integer deliveryId, 
			@RequestParam(required = false) Integer voucherId, 
			@RequestParam(required = false) String shippingAddress)
	{
		Person user = (Person) session.getAttribute("user");
		
		Order order = new Order();
		order.setShippingAddress(shippingAddress);
		LocalDateTime now = LocalDateTime.now().withNano((LocalDateTime.now().getNano() / 1000) * 1000);
		order.setOrderDate(now);
		order.setLines(new ArrayList<>());


		Product product = productService.findById(productId).get();
		
		OrderLine line = new OrderLine();
		line.setOrder(order);
		line.setProduct(product);
		line.setQuantity(1);
		order.getLines().add(line);
	
		Delivery deli = deliveryService.findById(deliveryId).get();
		order.setDelivery(deli);
		order.setUser(user);
		
		Payment payment = new Payment();
		payment.setPaymentMethod("VNPay");
		payment.setTotal(product.getPrice());
		
		order.setOrderStatus(OrderStatus.PENDING);
		order.setPayment(payment);
		if (voucherId != null)
		{
			Voucher voucher = voucherService.findById(voucherId).get();
			voucher.setActive(0);
			voucher.setOrder(order);
			session.setAttribute("orderVoucher", voucher);
		}
		session.setAttribute("checkingOutOrder", order);
		session.setAttribute("single", 1);
		session.setAttribute("productId", productId);
		String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnpayService.createOrder(request, product.getPrice(), "DonHang_"+order.getOrderDate(), baseUrl);
        System.out.println(order.getShippingAddress());
		return new ResponseEntity<>(vnpayUrl,HttpStatus.OK);
	}
}
