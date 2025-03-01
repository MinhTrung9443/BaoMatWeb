package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import vn.iotstar.entity.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Integer> {
	// Lấy danh sách categoryName của tất cả sản phẩm
    @Query("SELECT DISTINCT p.category.categoryName FROM Product p WHERE p.category.categoryName IS NOT NULL")
    List<String> findDistinctCategoryNamesFromProducts();
	@Query("SELECT DISTINCT p.brandOrigin FROM Product p WHERE p.brandOrigin IS NOT NULL")
	List<String> findDistinctOrigins();

	@Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL")
    List<String> findDistinctBrands();
	
	@Query("SELECT p FROM Product p " + "WHERE p.stock > 0 " + "ORDER BY p.warehouseDateFirst DESC")
	List<Product> findTop20ByOrderByWarehouseDateFirstDescWithStock(Pageable pageable);

	@Query("SELECT p FROM Product p " + "LEFT JOIN p.feedbacks f " + "WHERE p.stock > 0 " + "GROUP BY p "
			+ "ORDER BY COALESCE(AVG(f.rating), 0) DESC")
	Page<Product> findTopProductsByAverageRating(Pageable pageable);
	

	@Query("SELECT p FROM Product p " + "LEFT JOIN p.favourite f "
			+ "WHERE p.stock > 0 " + "GROUP BY p " + "ORDER BY COUNT(f) DESC")
	List<Product> findTop20ByFavouriteCount(Pageable pageable);

	@Query("SELECT ol.product FROM OrderLine ol " + "WHERE ol.product.stock > 0 " + "GROUP BY ol.product "
			+ "ORDER BY SUM(ol.quantity) DESC")
	List<Product> findTop20BySalesQuantity(Pageable pageable);

	@Query("SELECT p FROM Product p " + "LEFT JOIN p.favourite f "
			+ "WHERE p.category.categoryId = :categoryId AND p.stock > 0 " + "GROUP BY p " + "ORDER BY COUNT(f) DESC")
	List<Product> findTopProductsByCategory(@Param("categoryId") Long categoryId, Pageable pageable);

	// Lọc theo khoảng giá
	@Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
	Page<Product> findByPriceRange(@Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice, Pageable pageable);

	// Tìm kiếm theo brand
	Page<Product> findByBrandContaining(String brand, Pageable pageable);

	// Tìm kiếm theo category
	@Query("SELECT p FROM Product p JOIN p.category c WHERE TRIM(c.categoryName) = :categoryName")
	Page<Product> findByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

	// Tìm kiếm theo nguồn gốc (origin)
	Page<Product> findByBrandOriginContaining(String brandOrigin, Pageable pageable);

	// Tìm sản phẩm theo tên, không phân biệt chữ hoa/thường
	List<Product> findByProductNameContainingIgnoreCase(String productName);

	@Query("SELECT p FROM Product p " + "WHERE (:minPrice IS NULL OR p.price >= :minPrice) "
			+ "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " + "AND (:brand IS NULL OR p.brand = :brand) "
			+ "AND (:brandOrigin IS NULL OR p.brandOrigin = :brandOrigin) "
			+ "AND (:categoryName IS NULL OR p.category.categoryName LIKE %:categoryName%)")
	List<Product> searchProductsWithMultipleKeywords(@Param("minPrice") Integer minPrice,
			@Param("maxPrice") Integer maxPrice, @Param("brand") String brand, @Param("brandOrigin") String brandOrigin,
			@Param("categoryName") String categoryName);

	@Query("SELECT p FROM Product p WHERE p.stock > 0")
	Page<Product> findAllAvailable(Pageable pageable);

	public Page<Product> findByProductNameContaining(String keyword, Pageable pageable);

	@Query("SELECT p FROM Product p " + "JOIN p.category c " + "WHERE (:minPrice IS NULL OR p.price >= :minPrice) "
			+ "AND (:maxPrice IS NULL OR p.price <= :maxPrice) "
			+ "AND (:brandOrigin IS NULL OR p.brandOrigin LIKE CONCAT('%', :brandOrigin, '%')) "
			+ "AND (:brand IS NULL OR p.brand LIKE CONCAT('%', :brand, '%')) "
			+ "AND (:categoryName IS NULL OR c.categoryName LIKE CONCAT('%', :categoryName, '%'))")
	Page<Product> findByFilters(@Param("minPrice") Integer minPrice, @Param("maxPrice") Integer maxPrice,
			@Param("brandOrigin") String brandOrigin, @Param("brand") String brand,
			@Param("categoryName") String categoryName, Pageable pageable);

	Page<Product> findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String productName, String description,
			Pageable pageable);

}
