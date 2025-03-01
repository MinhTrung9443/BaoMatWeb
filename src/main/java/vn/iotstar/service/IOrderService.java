package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Order;
import vn.iotstar.enums.OrderStatus;

@Service
public interface IOrderService {

	int countByOrderStatus(int id,OrderStatus status);

	Page<Order> findByOrderStatus(int id,OrderStatus status, Pageable page);

	Page<Order> findAllByUserId(int id, Pageable page);

	Optional<Order> findById(Integer id);

	<S extends Order> S save(S entity);
	
	Page<Order> findByDeliveryIdAndStatus(int deliveryId, OrderStatus status, Pageable pageable);

	Page<Order> findByUserIdAndOrderStatusIn(int id, List<OrderStatus> statuses, Pageable page);
	
	List<Order> findByDelivery_DeliveryId(int deliveryId);

	
}
