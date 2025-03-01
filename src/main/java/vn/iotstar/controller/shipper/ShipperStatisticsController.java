package vn.iotstar.controller.shipper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Account;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderAssignment;
import vn.iotstar.entity.Shipper;
import vn.iotstar.enums.OrderStatus;
import vn.iotstar.service.IOrderService;
import vn.iotstar.service.IShipperService;
import vn.iotstar.service.impl.OrderAssignmentService;

@Controller
public class ShipperStatisticsController {
    @Autowired
    private IShipperService shipperService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private OrderAssignmentService orderAssignmentService;

    @GetMapping("/Shipper/statistics")
    public String getStatistics(Model model, HttpSession session) {
        // Lấy tài khoản từ session
        Account account = (Account) session.getAttribute("account");

        // Tìm shipper từ tài khoản
        Shipper shipper = shipperService.findByAccount_AccountId(account.getAccountId());

        if (shipper == null) {
            model.addAttribute("error", "Không tìm thấy thông tin Shipper.");
            return "error"; 
        }

        // Lấy danh sách OrderAssignment của shipper
        List<OrderAssignment> orderAssignments = orderAssignmentService.findByShipper(shipper);

        // Khởi tạo bộ đếm cho trạng thái đã giao thành công và chưa thành công
        int completedCount = 0;
        int shippingCount = 0;

        // Duyệt qua tất cả các OrderAssignment để thống kê trạng thái của từng đơn hàng
        for (OrderAssignment orderAssignment : orderAssignments) {
            Order order = orderAssignment.getOrder();
            if (order.getOrderStatus() == OrderStatus.COMPLETEDSHIPPER) {
                completedCount++;
            } else if (order.getOrderStatus() == OrderStatus.SHIPPING) {
                shippingCount++;
            }
        }

        // Tính toán tỷ lệ phần trăm
        int totalOrders = completedCount + shippingCount;
        double completedPercentage = totalOrders > 0 ? (completedCount * 100.0 / totalOrders) : 0;
        double shippingPercentage = totalOrders > 0 ? (shippingCount * 100.0 / totalOrders) : 0;

        // Thêm dữ liệu vào model để hiển thị trên view
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("shippingCount", shippingCount);
        model.addAttribute("completedPercentage", completedPercentage);
        model.addAttribute("shippingPercentage", shippingPercentage);

        return "Shipper/statistics"; // View để hiển thị biểu đồ
    }
    
    @GetMapping("/Shipper/statistics1")
    public String getStatisticsDate(Model model, HttpSession session) {
        // Lấy tài khoản từ session
        Account account = (Account) session.getAttribute("account");

        // Tìm shipper từ tài khoản
        Shipper shipper = shipperService.findByAccount_AccountId(account.getAccountId());

        if (shipper == null) {
            model.addAttribute("error", "Không tìm thấy thông tin Shipper.");
            return "error"; 
        }

        // Lấy danh sách OrderAssignment của shipper
        List<OrderAssignment> orderAssignments = orderAssignmentService.findByShipper(shipper);

        // Lọc các đơn hàng đã giao thành công
        List<Order> completedOrders = orderAssignments.stream()
            .map(OrderAssignment::getOrder)
            .filter(order -> order.getOrderStatus() == OrderStatus.COMPLETEDSHIPPER)
            .collect(Collectors.toList());

        // Phân nhóm theo tháng (hoặc ngày, năm)
        Map<String, Long> completedOrdersByMonth = completedOrders.stream()
            .collect(Collectors.groupingBy(
                order -> order.getCompletionTime().getMonth().toString() + " " + order.getCompletionTime().getYear(),
                Collectors.counting()
            ));

        System.out.println("Completed Orders By Month: " + completedOrdersByMonth);

        // Chuyển đổi Map thành JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonCompletedOrdersByMonth = objectMapper.writeValueAsString(completedOrdersByMonth);

            System.out.println("Chuỗi JSON được gửi tới Thymeleaf: " + jsonCompletedOrdersByMonth);
            
            model.addAttribute("jsonCompletedOrdersByMonth", jsonCompletedOrdersByMonth);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi xử lý dữ liệu JSON.");
            return "error";
        }

        return "Shipper/statisticsDate"; 
    }

}
