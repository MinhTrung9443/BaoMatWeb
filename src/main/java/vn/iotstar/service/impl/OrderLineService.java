package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.repository.IOrderLineRepository;
import vn.iotstar.service.IOrderLineService;

@Service
public class OrderLineService implements IOrderLineService {
	
	@Autowired
	private IOrderLineRepository orderLineRepository;

	@Override
	public Integer findTotalQuantityByProductId(Integer productId) {
		return orderLineRepository.findTotalQuantityByProductId(productId);
	}
}

