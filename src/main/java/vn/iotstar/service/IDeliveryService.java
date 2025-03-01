package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Delivery;

public interface IDeliveryService {

	void deleteById(Integer id);

	long count();

	Optional<Delivery> findById(Integer id);

	List<Delivery> findAll();

	Page<Delivery> findAll(Pageable pageable);

	<S extends Delivery> S save(S entity);

	Delivery findByDeliveryName(String deliveryName);

}
