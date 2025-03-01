package vn.iotstar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderAssignment;
import vn.iotstar.entity.Shipper;
import vn.iotstar.repository.IOrderAssignment;

@Service
public class OrderAssignmentService {
    @Autowired
    private IOrderAssignment orderAssignmentRepository;

    public void save(OrderAssignment orderAssignment) {
        orderAssignmentRepository.save(orderAssignment);
    }
    public List<Order> findByShipperId(int shipperId) {
        return orderAssignmentRepository.findByShipperId(shipperId);
    }
    public List<OrderAssignment> findByShipper(Shipper shipper) {
        return orderAssignmentRepository.findByShipper(shipper);
    }
}
