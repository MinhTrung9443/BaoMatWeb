package vn.iotstar.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.iotstar.entity.CartItem;


@Service
public interface ICartItemService {

	<S extends CartItem> S save(S entity);

	void deleteById(Integer id);

	Optional<CartItem> findById(Integer id);

	void delete(CartItem entity);

	
}
