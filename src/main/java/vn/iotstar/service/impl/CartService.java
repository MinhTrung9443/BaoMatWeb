package vn.iotstar.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.CartItem;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.ShoppingCart;
import vn.iotstar.repository.ICartItemRepository;
import vn.iotstar.repository.IProductRepository;
import vn.iotstar.repository.IShoppingCartRepository;
import vn.iotstar.service.ICartService;

@Service
public class CartService implements ICartService {
	@Autowired
	private IShoppingCartRepository cartRepository;
	@Autowired
	private ICartItemRepository cartItemRepository;
	@Autowired
	private IProductRepository productRepository;

	@Override
	public <S extends ShoppingCart> S save(S entity) {
		return cartRepository.save(entity);
	}

	@Override
	public Optional<ShoppingCart> findByUserId(int id) {
		return cartRepository.findByUserId(id);
	}

	@Override
	public Optional<ShoppingCart> findById(Integer id) {
		return cartRepository.findById(id);
	}
	
	@Override
	public void deleteAllCartItem(Integer id)
	{
		ShoppingCart cart = this.findByUserId(id).get();
		
		for (CartItem item : cart.getItems())
		{
			Product product = item.getProduct();
			long stock = product.getStock();
			product.setStock(stock - item.getQuantity());
			productRepository.save(product);
			item.setQuantity(0);
			cartItemRepository.save(item);
		}
		
	}
}
