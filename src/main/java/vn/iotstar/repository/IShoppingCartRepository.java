package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.ShoppingCart;

@Repository
public interface IShoppingCartRepository extends JpaRepository<ShoppingCart, Integer>{
	Optional<ShoppingCart> findByUserId(int id);
}
