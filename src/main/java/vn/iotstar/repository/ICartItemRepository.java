package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.CartItem;

@Repository
public interface ICartItemRepository extends JpaRepository<CartItem, Integer> {
	 Optional<CartItem> findByCart_ShoppingCartIdAndProduct_ProductId(int shoppingCartId, int productId);
}
