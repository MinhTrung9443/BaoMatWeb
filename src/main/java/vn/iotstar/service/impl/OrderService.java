package vn.iotstar.service.impl;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import vn.iotstar.entity.Order;
import vn.iotstar.enums.OrderStatus;
import vn.iotstar.repository.IOrderRepository;
import vn.iotstar.service.IOrderService;

@Service
public class OrderService implements IOrderService{
	@Autowired
	IOrderRepository orderRepository;

	@Override
	public Page<Order> findByOrderStatus(int id, OrderStatus status, Pageable page) {
		return orderRepository.findByUserIdAndOrderStatus(id,status, page);
	}

	@Override
	public int countByOrderStatus(int id,OrderStatus status) {
		return orderRepository.countByUserIdAndOrderStatus(id,status);
	}

	@Override
	public Page<Order> findAllByUserId(int id, Pageable page) {
		return orderRepository.findAllByUserId(id, page);
	}

	@Override
	public Optional<Order> findById(Integer id) {
		return orderRepository.findById(id);
	}

	@Override
	public <S extends Order> S save(S entity) {
		return orderRepository.save(entity);
	}


	public Page<Order> getOrdersPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }
    
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public long countOrdersByStatus(OrderStatus status) {
        return orderRepository.countByOrderStatus(status);
    }

    @Override
    public Page<Order> findByDeliveryIdAndStatus(int deliveryId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByDelivery_DeliveryIdAndOrderStatus(deliveryId, status, pageable);
    }

    
    public Page<Order> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByOrderStatus(status, pageable);}
    
    @Transactional
    public boolean updateOrderStatus(int orderId, OrderStatus newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId); // Tìm đơn hàng theo ID
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            
            // Kiểm tra trạng thái hiện tại của đơn hàng và chỉ cho phép chuyển từ trạng thái hợp lệ
            OrderStatus currentStatus = order.getOrderStatus();

            // Chỉ cho phép chuyển trạng thái nếu trạng thái hiện tại hợp lệ
            switch (currentStatus) {
                case PENDING:
                    if (newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED) {
                        order.setOrderStatus(newStatus);
                        orderRepository.save(order);
                        return true;
                    }
                    break;
                case CONFIRMED:
                    if (newStatus == OrderStatus.REFUNDED || newStatus == OrderStatus.CANCELLED) {
                        order.setOrderStatus(newStatus);
                        orderRepository.save(order);
                        return true;
                    }
                    break;
                case REFUNDED:
                case CANCELLED:
                    // Không cho phép thay đổi trạng thái nữa
                    return false;
                // Thêm các trạng thái khác nếu cần
                default:
                    return false;
            }
        }
        return false;
    }



	public List<Order> findCompletedOrdersByMonth(int year, int month) {
	    // Sử dụng YearMonth để tính số ngày trong tháng
	    YearMonth yearMonth = YearMonth.of(year, month);
	    int lastDayOfMonth = yearMonth.lengthOfMonth(); // Số ngày trong tháng

	    // Tạo thời gian bắt đầu và kết thúc của tháng
	    LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0, 0);
	    LocalDateTime endOfMonth = LocalDateTime.of(year, month, lastDayOfMonth, 23, 59, 59, 999999999);

	    // Trả về danh sách các đơn hàng đã hoàn thành trong khoảng thời gian từ đầu đến cuối tháng
	    return orderRepository.findByOrderStatusAndCompletionTimeBetween(
	        OrderStatus.COMPLETED,
	        startOfMonth,
	        endOfMonth
	    );
	}

	@Override
	public Page<Order> findByUserIdAndOrderStatusIn(int id, List<OrderStatus> statuses, Pageable page) {
		return orderRepository.findByUserIdAndOrderStatusIn(id, statuses, page);
	}
	
	@Override
	public List<Order> findByDelivery_DeliveryId(int deliveryId) {
		return orderRepository.findByDelivery_DeliveryId(deliveryId);
	}
	
}
