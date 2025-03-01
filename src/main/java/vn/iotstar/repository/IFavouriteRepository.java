package vn.iotstar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Favourite;


@Repository
public interface IFavouriteRepository extends JpaRepository<Favourite, Integer>{
	Optional<Favourite> findByUserIdAndProduct_ProductId(int userId, int productId);
	Page<Favourite> findAllByUserId(int id, Pageable pageable);
	int countAllByUserId(int id);
}
