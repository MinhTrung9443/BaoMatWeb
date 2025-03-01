package vn.iotstar.controller.vendor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.dto.RevenueByMonthDTO;
import vn.iotstar.entity.Order;
import vn.iotstar.enums.OrderStatus;
import vn.iotstar.service.IRevenueService;
import vn.iotstar.service.impl.CategoryRevenueStats;
import vn.iotstar.service.impl.OrderService;
import vn.iotstar.service.impl.RevenueService;
import vn.iotstar.service.impl.RevenueStats;

@Controller
public class RevenueController {

	@Autowired
    private RevenueService revenueService;

	@Autowired
    private OrderService orderService;

    
	@GetMapping("/revenue-chart")
	public String getOrderStatusChart(Model model) {
	    long pendingCount = orderService.countOrdersByStatus(OrderStatus.PENDING);
	    long confirmedCount = orderService.countOrdersByStatus(OrderStatus.CONFIRMED);
	    long shippingCount = orderService.countOrdersByStatus(OrderStatus.SHIPPING);
	    long completedCount = orderService.countOrdersByStatus(OrderStatus.COMPLETED);
	    long refundedCount = orderService.countOrdersByStatus(OrderStatus.REFUNDED);
	    long cancelledCount = orderService.countOrdersByStatus(OrderStatus.CANCELLED);

	    model.addAttribute("pendingCount", pendingCount);
	    model.addAttribute("confirmedCount", confirmedCount);
	    model.addAttribute("shippingCount", shippingCount);
	    model.addAttribute("completedCount", completedCount);
	    model.addAttribute("refundedCount", refundedCount);
	    model.addAttribute("cancelledCount", cancelledCount);

        RevenueStats revenueStats = revenueService.getRevenueStats();
        model.addAttribute("totalProductsSold", revenueStats.getTotalProductsSold());
        model.addAttribute("totalRevenue", revenueStats.getTotalRevenue());
	    return "/Vendor/revenue/chart";  // Tên của view cho biểu đồ
	}
	@GetMapping("/revenue")
	public String showRevenueStats(Model model) {
	    List<CategoryRevenueStats> categoryStatsList = revenueService.getCategoryRevenueStats();
	    System.out.println("Category Stats: " + categoryStatsList); // In ra để kiểm tra
	    model.addAttribute("categoryStatsList", categoryStatsList);  
	    return "/Vendor/revenue/revenue";
	}
	
	
	@GetMapping("/revenue-by-month")
	public String getRevenueByMonth(Model model) {
	    List<RevenueByMonthDTO> revenueList = revenueService.getRevenueByMonth();

	    model.addAttribute("data", revenueList); // Truyền toàn bộ danh sách sang frontend

	    return "/Vendor/revenue/revenue_month"; // Trang hiển thị biểu đồ
	}


}
