package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Product;

@Service
public interface IProductService {
	List<String>findByCategoryName();
	List<String>findByOriginBrand();
	List<String> findByBrand();
	List<Product> findTop20BySalesQuantity();

	List<Product> findTop20ByFavouriteCount();

	Page<Product> findTop20ByAverageRating();

	List<Product> findTop20ByOrderByWarehouseDateFirstDesc();

	Optional<Product> findById(Integer id);

	void decreaseProductStock(int productId);

	List<Product> findTop5ByFavouriteCount(long categoryId);

	<S extends Product> S save(S entity);

	Page<Product> getProductsByCategoryName(String categoryName);

	List<Product> getProductsByName(String Name);

	Page<Product> getProductsByBrand(String brand);

	Page<Product> getProductsByPriceRange(int minPrice, int maxPrice);

	List<Product> getAllProducts();

	Page<Product> getProductsByBrandOrigin(String brandOrigin);

	List<Product> searchProducts(String query);

	List<Product> searchProductsWithMultipleKeywords(Integer minPrice, Integer maxPrice, String brand,
			String brandOrigin, String categoryName);

	Product getProductById(int productId);

	Page<Product> searchProductsWithMultipleKeywords(Integer minPrice, Integer maxPrice, String brand,
			String brandOrigin, String categoryName, Pageable pageable);

	Page<Product> getProductsByName(String keyword, Pageable pageable);

	Page<Product> getAllProducts(Pageable pageable);

	Page<Product> getProductsByBrand(String brand, Pageable pageable);

	Page<Product> getProductsByCategoryName(String categoryName, Pageable pageable);

	Page<Product> getProductsByPriceRange(int minPrice, int maxPrice, Pageable pageable);

	Page<Product> getProductsByBrandOrigin(String brandOrigin, Pageable pageable);

	List<Product> getTop10BestSellingProducts();
}
