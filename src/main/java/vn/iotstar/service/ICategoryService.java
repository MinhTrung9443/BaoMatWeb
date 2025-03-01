package vn.iotstar.service;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import vn.iotstar.entity.Category;
@Service
public interface ICategoryService {

	long count();

	Optional<Category> findById(int id);

	List<Category> findAll();

	Page<Category> findAll(Pageable pageable);

	<S extends Category> S save(S entity);

	void deleteById(Integer id);

	Category findByCategoryName(String categoryName);
	List<Category> findAllById(int id);
}
