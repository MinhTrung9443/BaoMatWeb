package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Favourite;

public interface IFavouriteService {

	void deleteById(Integer id);

	<S extends Favourite> S save(S entity);

	Optional<Favourite> findByUserId(int userId, int productId);

	Page<Favourite> findAllByUserId(int Id, Pageable page);

	int countAllByUserId(int id);



}
