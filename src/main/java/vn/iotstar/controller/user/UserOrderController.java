package vn.iotstar.controller.user;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderLine;
import vn.iotstar.entity.Person;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Vendor;
import vn.iotstar.enums.OrderStatus;
import vn.iotstar.service.IOrderService;
import vn.iotstar.service.IProductService;

import org.springframework.web.bind.annotation.PostMapping;




@Controller
@RequestMapping("/User/Order")
public class UserOrderController {
	@Autowired
	private IOrderService orderService;
	@Autowired
	private IProductService productService;
	
	@GetMapping({"","/page"})
    public String getOrderHistory(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "tab", defaultValue = "don-cho-xac-nhan") String tab,
            Model model, HttpSession session) {

        Person user = (Person) session.getAttribute("user");
        if (user == null) {
            return "redirect:user/signin";
        }
        
        int customerId=0;
        if (user.getAccount().getRole().getRoleId() == 2)
        {
        	User u = (User) user;
        	 customerId = u.getId();
        }
        else if (user.getAccount().getRole().getRoleId() == 3) {
        	Vendor u = (Vendor) user;
        	 customerId = u.getId();
        }

        Page<Order> orders;
        int pageSize = 10;
        Pageable page = PageRequest.of(pageNo-1, pageSize,Sort.by(Sort.Order.desc("orderId")));
        int count = 0;
        switch (tab) {
            case "don-cho-xac-nhan":
                orders = orderService.findByOrderStatus(customerId, OrderStatus.PENDING,page ); 
                count=orderService.countByOrderStatus(customerId, OrderStatus.PENDING);
                break;
            case "don-da-xac-nhan":
                orders = orderService.findByOrderStatus(customerId, OrderStatus.CONFIRMED,page ); 
                count=orderService.countByOrderStatus(customerId, OrderStatus.CONFIRMED);
                break;
            case "don-dang-van-chuyen":
            	List<OrderStatus> list = Arrays.asList(OrderStatus.SHIPPING, OrderStatus.COMPLETEDSHIPPER);
                orders = orderService.findByUserIdAndOrderStatusIn(customerId, list,page ); 
                count=orderService.countByOrderStatus(customerId, OrderStatus.SHIPPING);
                break;
            case "don-da-giao":
                orders = orderService.findByOrderStatus(customerId, OrderStatus.COMPLETED,page ); 
                count=orderService.countByOrderStatus(customerId, OrderStatus.COMPLETED);
                break;
            case "don-huy":
                orders = orderService.findByOrderStatus(customerId, OrderStatus.CANCELLED,page );
                count=orderService.countByOrderStatus(customerId, OrderStatus.CANCELLED);
                break;
            default:
            	orders = orderService.findByOrderStatus(customerId, OrderStatus.PENDING,page ); 
                count=orderService.countByOrderStatus(customerId, OrderStatus.PENDING);
                break;
        }
        model.addAttribute("orders", orders);
        model.addAttribute("tab", tab);
        model.addAttribute("currentPage", pageNo);
	    model.addAttribute("pageSize", pageSize);
	    model.addAttribute("totalItems", count);
	    model.addAttribute("totalPages", (int) Math.ceil((double) count / pageSize));
        return "User/UserOrder";
    }
	  @GetMapping("/detail/{orderId}")
	    public String orderDetail(@PathVariable int orderId, ModelMap model, HttpSession session) {
	        // 1. Lấy thông tin người dùng đang đăng nhập từ session
	        Person loggedInPerson = (Person) session.getAttribute("user");

	        // Nếu người dùng chưa đăng nhập, chuyển hướng về trang đăng nhập
	        if (loggedInPerson == null) {
	            return "redirect:/login"; // Hoặc trang đăng nhập của User
	        }
	        // 2. Lấy đơn hàng theo orderId
	        Optional<Order> orderOptional = orderService.findById(orderId);
	        // Nếu không tìm thấy đơn hàng
	        if (orderOptional.isEmpty()) {
	            // Ném ra lỗi 404 hoặc chuyển hướng đến trang lỗi tùy chỉnh
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng");
	            // hoặc return "error/404"; // nếu bạn có trang lỗi 404.html
	        }
	        Order order = orderOptional.get();
	        // 3. Kiểm tra quyền sở hữu đơn hàng
	        // Lấy ID của người dùng hiện tại. Cần điều chỉnh cách lấy ID tùy theo cấu trúc User/Vendor của bạn
	        int currentUserId = 0;
	        if (loggedInPerson.getAccount().getRole().getRoleId() == 2 && loggedInPerson instanceof User) {
	            User u = (User) loggedInPerson;
	            currentUserId = u.getId();
	        } else if (loggedInPerson.getAccount().getRole().getRoleId() == 3 && loggedInPerson instanceof Vendor) {
	            // Nếu Vendor cũng có thể xem đơn hàng của họ theo cách này
	            Vendor v = (Vendor) loggedInPerson;
	            currentUserId = v.getId(); // Giả sử Vendor cũng có getId() tương tự
	        } else {
	            // Vai trò không xác định hoặc không được phép xem đơn hàng theo cách này
	            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền truy cập tài nguyên này.");
	        }
	        if (order.getUser() == null || order.getUser().getId() != currentUserId) {
	            // Nếu không khớp, người dùng này không sở hữu đơn hàng
	            // Ném ra lỗi 403 Forbidden hoặc chuyển hướng
	            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bạn không có quyền xem đơn hàng này.");
	            // hoặc return "error/403"; // nếu bạn có trang lỗi 403.html
	        }
	        // Nếu mọi thứ ổn, thêm đơn hàng vào model và trả về view
	        model.addAttribute("order", order);
	        return "User/OrderDetail";
	    }
	@PostMapping("/cancel")
	public ResponseEntity<?> postMethodName(ModelMap model, HttpSession session, @RequestParam int orderId) {
		
		try {
			Order order = orderService.findById(orderId).get();
			
			order.setOrderStatus(OrderStatus.CANCELLED);
			
			for (OrderLine line : order.getLines())
			{
				Product product = line.getProduct();
				long stock = product.getStock();
				product.setStock(stock+ line.getQuantity());
				productService.save(product);
			}
			
			orderService.save(order);
			
			return new ResponseEntity<>("Hủy đơn thành công",HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Hủy đơn khong thành công",HttpStatus.BAD_REQUEST);
		}
	}
	@PostMapping("/complete")
	public ResponseEntity<?> complete(ModelMap model, HttpSession session, @RequestParam int orderId) {
		
		try {
			Order order = orderService.findById(orderId).get();
			
			order.setOrderStatus(OrderStatus.COMPLETED);
			order.setCompletionTime(LocalDateTime.now());
			orderService.save(order);
			
			return new ResponseEntity<>("Cảm ơn quý khách đã sử dụng dịch vụ",HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Có lỗi! xin lỗi quý khách vì sự bất tiện này! Mời quý khách quy lại sau!",HttpStatus.BAD_REQUEST);
		}
	}
}
