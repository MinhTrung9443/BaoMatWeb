package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderAssignment;
import vn.iotstar.entity.Shipper;

@Repository
public interface IOrderAssignment extends JpaRepository<OrderAssignment, Integer> {
    List<Order> findByShipperId(int shipperId);
    List<OrderAssignment> findByShipper(Shipper shipper);
}
