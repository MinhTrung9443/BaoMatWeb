package vn.iotstar.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.CartItem;
import vn.iotstar.repository.ICartItemRepository;
import vn.iotstar.service.ICartItemService;

@Service
public class CartItemService implements ICartItemService{
	@Autowired
	ICartItemRepository cartRepository;
	@Override
	public <S extends CartItem> S save(S entity) {
		return cartRepository.save(entity);
	}

	@Override
	public Optional<CartItem> findById(Integer id) {
		return cartRepository.findById(id);
	}

	@Override
	public void deleteById(Integer id) {
		cartRepository.deleteById(id);
	}

	@Override
	public void delete(CartItem entity) {
		cartRepository.delete(entity);
	}

	
	
	
}	
