package vn.iotstar.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Order;
import vn.iotstar.enums.OrderStatus;
import vn.iotstar.service.impl.OrderService;

@Controller
public class OrderController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/Vendor/orders")
	public String getAllOrders(@RequestParam(required = false) OrderStatus status,
	                           @RequestParam(defaultValue = "0") int page,
	                           @RequestParam(defaultValue = "5") int size,
	                           Model model) {
	    Pageable pageable = PageRequest.of(page, size);
	    Page<Order> ordersPage;

	    long totalOrders = 0; // Biến lưu tổng số đơn hàng theo trạng thái
	    if (status != null) {
	        ordersPage = orderService.getOrdersByStatus(status, pageable);
	        totalOrders = orderService.countOrdersByStatus(status);
	    } else {
	        ordersPage = orderService.getAllOrders(pageable);
	        totalOrders = orderService.getOrdersPage(page, size).getTotalElements();
	    }

	    model.addAttribute("orders", ordersPage.getContent());
	    model.addAttribute("ordersPage", ordersPage);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", ordersPage.getTotalPages());
	    model.addAttribute("status", status);
	    model.addAttribute("totalOrders", totalOrders); // Thêm tổng số đơn hàng vào model

	    return "/Vendor/order/order_list";
	}




	@PostMapping("/Vendor/updateOrderStatus")
	public String updateOrderStatus(@RequestParam int orderId, @RequestParam OrderStatus newStatus, Model model) { // Đổi kiểu dữ liệu orderId thành Long
	    boolean updated = orderService.updateOrderStatus(orderId, newStatus);
	    if (updated) {
	        model.addAttribute("message", "Cập nhật trạng thái đơn hàng thành công!");
	    } else {
	        model.addAttribute("message", "Không thể cập nhật trạng thái đơn hàng!");
	    }
	    return "redirect:/Vendor/orders";
	}

}
