package vn.iotstar.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import vn.iotstar.entity.Category;
import java.util.List;


@Repository
public interface ICategoryRepository extends JpaRepository<Category, Integer> {
	List<Category> findByCategoryId(int categoryId);
	Category findByCategoryName(String categoryName);
	List<Category> findByActive(int active);
}
