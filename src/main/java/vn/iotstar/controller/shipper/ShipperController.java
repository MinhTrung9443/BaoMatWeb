package vn.iotstar.controller.shipper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
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
public class ShipperController {
	@Autowired
    private IShipperService shipperService;

    @PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private OrderAssignmentService orderAssignmentService;
	
    @Autowired
    private IOrderService orderService;

    @GetMapping("/Shipper")
    public String home() {
        return "Shipper/index";
    }
    
    @GetMapping("/Shipper1")
    public String getCompletedOrders(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size,
        Model model, HttpSession session) {
        
        Account account = (Account) session.getAttribute("account");
        Shipper shipper = shipperService.findByAccount_AccountId(account.getAccountId());

        Shipper shipper1 = shipperService.findByPersonId(shipper.getId());
        if (shipper1 == null) {
            model.addAttribute("error", "Không tìm thấy thông tin Shipper.");
            return "error";
        }
        int deliveryId = shipper1.getDelivery().getDeliveryId();

        Page<Order> completedOrdersPage = orderService.findByDeliveryIdAndStatus(
            deliveryId, OrderStatus.CONFIRMED, PageRequest.of(page, size));

        model.addAttribute("completedOrders", completedOrdersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", completedOrdersPage.getTotalPages());

        return "Shipper/order/list";
    }

    @PostMapping("/Shipper/confirmDelivery")
    public String confirmDelivery(@RequestParam int orderId, HttpSession session) {
        Order order = orderService.findById(orderId).get();
        if (order != null) {
            order.setOrderStatus(OrderStatus.SHIPPING); // Cập nhật trạng thái đơn hàng
            orderService.save(order); // Lưu lại trạng thái mới
            
            Account account = (Account) session.getAttribute("account");
            Shipper shipper = shipperService.findByAccount_AccountId(account.getAccountId());
            
            OrderAssignment orderAssignment = new OrderAssignment();
            orderAssignment.setOrder(order);
            orderAssignment.setShipper(shipper);
            
            orderAssignmentService.save(orderAssignment); // Đảm bảo bạn đã khai báo OrderAssignmentService
            
        }

        return "redirect:/Shipper1";
    }

    @GetMapping("/Shipper/delivered")
    public String getShipperConfirmedOrders(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size,
        Model model, HttpSession session) {
        
        Account account = (Account) session.getAttribute("account");
        Shipper shipper = shipperService.findByAccount_AccountId(account.getAccountId());
        
        if (shipper == null) {
            model.addAttribute("error", "Không tìm thấy thông tin Shipper.");
            return "error";
        }
        int deliveryId = shipper.getDelivery().getDeliveryId();

        Pageable pageable = PageRequest.of(page, size);
        String jpql = "SELECT o FROM Order o WHERE o.delivery.deliveryId = :deliveryId AND o.orderStatus IN :statuses";
        List<OrderStatus> statuses = Arrays.asList(OrderStatus.SHIPPING, OrderStatus.COMPLETEDSHIPPER);

        TypedQuery<Order> query = entityManager.createQuery(jpql, Order.class)
                .setParameter("deliveryId", deliveryId)
                .setParameter("statuses", statuses);
        
        int totalRecords = query.getResultList().size();
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        List<Order> confirmedOrders = query.getResultList();

        model.addAttribute("confirmedOrders", confirmedOrders);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", (int) Math.ceil((double) totalRecords / size));

        return "Shipper/order/shipperOrder";
    }


    @PostMapping("/Shipper/order/{orderId}/updateStatus")
    public String updateOrderStatus(@PathVariable int orderId, @RequestParam String status, HttpSession session, Model model) {
        Account account = (Account) session.getAttribute("account");
        Shipper shipper = shipperService.findByAccount_AccountId(account.getAccountId());

        if (shipper == null) {
            model.addAttribute("error", "Không tìm thấy thông tin Shipper.");
            return "error"; 
        }

        Order order = orderService.findById(orderId).get();
        if (order == null) {
            model.addAttribute("error", "Đơn hàng không hợp lệ.");
            return "error";
        }

        if (order.getDelivery().getDeliveryId() != shipper.getDelivery().getDeliveryId()) {
            model.addAttribute("error", "Bạn không có quyền cập nhật đơn hàng này.");
            return "error";
        }

        if ("COMPLETEDSHIPPER".equals(status) && order.getOrderStatus().equals(OrderStatus.SHIPPING)) {
            order.setOrderStatus(OrderStatus.COMPLETEDSHIPPER);
            order.setCompletionTime(LocalDateTime.now());
            orderService.save(order);
        }

        return "redirect:/Shipper/delivered"; 
    }
}