package vn.iotstar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Delivery;
import vn.iotstar.repository.IDeliveryRepository;
import vn.iotstar.service.IDeliveryService;

@Service
public class DeliveryService implements IDeliveryService{
	
	@Autowired
	IDeliveryRepository deliveryRepository;	

	public DeliveryService(IDeliveryRepository deliveryRepository) {
		this.deliveryRepository = deliveryRepository;
	}

	@Override
	public <S extends Delivery> S save(S entity) {
		return deliveryRepository.save(entity);
	}

	@Override
	public Page<Delivery> findAll(Pageable pageable) {
		return deliveryRepository.findAll(pageable);
	}

	@Override
	public List<Delivery> findAll() {
		return deliveryRepository.findAll();
	}

	@Override
	public Optional<Delivery> findById(Integer id) {
		return deliveryRepository.findById(id);
	}

	@Override
	public long count() {
		return deliveryRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		deliveryRepository.deleteById(id);
	}

	@Override
	public Delivery findByDeliveryName(String deliveryName) {
		return deliveryRepository.findByDeliveryName(deliveryName);
	}

}
