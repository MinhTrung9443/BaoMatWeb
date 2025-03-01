package vn.iotstar.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.dto.ProductRequestDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.ProductImage;
import vn.iotstar.repository.ICategoryRepository;
import vn.iotstar.repository.IProductRepository;
import vn.iotstar.service.IProductService;

@Service
public class ProductService implements IProductService {
	@Autowired
	private IProductRepository productRepository;
	@Autowired
    private ICategoryRepository categoryRepository;
	@Override
	public List<String>findByCategoryName()
	{
		return productRepository.findDistinctCategoryNamesFromProducts();
	}
	@Override
	public List<String>findByOriginBrand()
	{
		return productRepository.findDistinctOrigins();
	}
	
	@Override
	public List<Product> findTop20ByOrderByWarehouseDateFirstDesc() {
		Pageable page = PageRequest.of(0, 20);
		return productRepository.findTop20ByOrderByWarehouseDateFirstDescWithStock(page);
	}
	@Override
	public List<String> findByBrand(){
		return productRepository.findDistinctBrands();
	}

	@Override
	public Page<Product> findTop20ByAverageRating() {
		Pageable page = PageRequest.of(0, 20);
		return productRepository.findTopProductsByAverageRating(page);
	}


	@Override
	public List<Product> findTop20ByFavouriteCount() {
		Pageable page = PageRequest.of(0, 20);
		return productRepository.findTop20ByFavouriteCount(page);
	}

	@Override
	public List<Product> findTop20BySalesQuantity() {
		Pageable page = PageRequest.of(0, 20);
		return productRepository.findTop20BySalesQuantity(page);
	}

	@Override
	public Optional<Product> findById(Integer id) {
		return productRepository.findById(id);
	}
	
	@Override
	public void decreaseProductStock(int productId)
	{
		Product product = this.findById(productId).get();
		long stock = product.getStock();
		product.setStock(stock - 1);
		productRepository.save(product);
	}

	@Override
	public List<Product> findTop5ByFavouriteCount(long categoryId) {
		Pageable page = PageRequest.of(0, 5);
		return productRepository.findTopProductsByCategory(categoryId,page);
	}

	@Override
	public <S extends Product> S save(S entity) {
		return productRepository.save(entity);
	}
	@Override
	public Page<Product> getProductsByPriceRange(int minPrice, int maxPrice, Pageable pageable) {
		return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
	}

	@Override
	public Page<Product> getProductsByBrand(String brand, Pageable pageable) {
		return productRepository.findByBrandContaining(brand, pageable);
	}

	@Override
	public Page<Product> getProductsByCategoryName(String categoryName, Pageable pageable) {
		return productRepository.findByCategoryName(categoryName, pageable);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Page<Product> getProductsByBrandOrigin(String brandOrigin, Pageable pageable) {
		return productRepository.findByBrandOriginContaining(brandOrigin, pageable); // Tìm theo brandOrigin
	}

	@Override
	public List<Product> searchProducts(String query) {
		return productRepository.findByProductNameContainingIgnoreCase(query);
	}

	@Override
	public List<Product> searchProductsWithMultipleKeywords(Integer minPrice, Integer maxPrice, String brand,
			String brandOrigin, String categoryName) {
		return productRepository.searchProductsWithMultipleKeywords(minPrice, maxPrice, brand, brandOrigin,
				categoryName);
	}

	@Override
	public Product getProductById(int productId) {
		return productRepository.findById(productId).orElse(null);
	}

	@Override
	public List<Product> getProductsByName(String Name) {
		// TODO Auto-generated method stub
		return productRepository.findByProductNameContainingIgnoreCase(Name);
	}

	@Override
	public Page<Product> getAllProducts(Pageable pageable) {
		return productRepository.findAll(pageable); // Trả về tất cả sản phẩm phân trang
	}

	@Override
	public Page<Product> getProductsByName(String keyword, Pageable pageable) {
		return productRepository.findByProductNameContaining(keyword, pageable);
	}

	@Override
	public Page<Product> searchProductsWithMultipleKeywords(Integer minPrice, Integer maxPrice, String brand,
			String brandOrigin, String categoryName, Pageable pageable) {
		// Áp dụng các bộ lọc và phân trang
		return productRepository.findByFilters(minPrice, maxPrice, brand, brandOrigin, categoryName, pageable);
	}

	@Override
	public Page<Product> getProductsByCategoryName(String categoryName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Product> getProductsByBrand(String brand) {
		// TODO Auto-generated method stub
		return null;
	}

	public Page<Product> searchProducts(String search, Pageable pageable) {
	    return productRepository.findByProductNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable);
	}

	@Override
	public Page<Product> getProductsByPriceRange(int minPrice, int maxPrice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<Product> getProductsByBrandOrigin(String brandOrigin) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Product> getTop10BestSellingProducts() {
		Pageable page = PageRequest.of(0, 20);
		return productRepository.findTop20BySalesQuantity(page);
	}
	
	public Page<Product> findAllAvailable(Pageable pageable) {
        return productRepository.findAllAvailable(pageable);
    }

	public void updateProduct(Integer productId, ProductRequestDTO productRequestDTO) {
	    Optional<Product> optionalProduct = productRepository.findById(productId);
	    if (optionalProduct.isPresent()) {
	        Product product = optionalProduct.get();

	        // Cập nhật các thông tin sản phẩm
	        product.setProductName(productRequestDTO.getProductName());
	        product.setPrice(productRequestDTO.getPrice());
	        product.setDescription(productRequestDTO.getDescription());
	        product.setBrand(productRequestDTO.getBrand());
	        product.setExpirationDate(productRequestDTO.getExpirationDate());
	        product.setManufactureDate(productRequestDTO.getManufactureDate());
	        product.setIngredient(productRequestDTO.getIngredient());
	        product.setInstruction(productRequestDTO.getInstruction());
	        product.setVolumeOrWeight(productRequestDTO.getVolumeOrWeight());
	        product.setBrandOrigin(productRequestDTO.getBrandOrigin());
	        product.setStock(productRequestDTO.getStock());
	        product.setWarehouseDateFirst(productRequestDTO.getWarehouse_date_first());

	        // Cập nhật thể loại
	        Category category = new Category();
	        category.setCategoryId(productRequestDTO.getCategoryId());
	        product.setCategory(category);

	       

	        // Lưu sản phẩm sau khi cập nhật
	        productRepository.save(product);
	    }
	}

	public Product createProduct(ProductRequestDTO productRequestDTO) {
		Category category = categoryRepository.findById(productRequestDTO.getCategoryId())
	            .orElseThrow(() -> new RuntimeException("Category not found"));

		 if (category.getActive() != 1) {
		        throw new RuntimeException("Category is not active. Product cannot be created.");
		    }
		 
	    Product product = new Product();
	    product.setProductId(productRequestDTO.getProductId());
	    product.setProductName(productRequestDTO.getProductName());
	    product.setPrice(productRequestDTO.getPrice());
	    product.setDescription(productRequestDTO.getDescription());
	    product.setBrand(productRequestDTO.getBrand());
	    product.setStock(productRequestDTO.getStock());
	    product.setBrandOrigin(productRequestDTO.getBrandOrigin());
	    product.setIngredient(productRequestDTO.getIngredient());
	    product.setInstruction(productRequestDTO.getInstruction());
	    product.setManufactureDate(productRequestDTO.getManufactureDate());
	    product.setExpirationDate(productRequestDTO.getExpirationDate());
	    product.setWarehouseDateFirst(productRequestDTO.getWarehouse_date_first());
	    product.setVolumeOrWeight(productRequestDTO.getVolumeOrWeight());
	    product.setCategory(category);

	    List<ProductImage> images = new ArrayList<>();
	    for (String url : productRequestDTO.getImageUrls()) {
	        ProductImage image = new ProductImage();
	        image.setImageUrl(url.trim());
	        image.setProduct(product);
	        images.add(image);
	    }
	    product.setImages(images);

	    return productRepository.save(product);
    }

	public void setStockToZero(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStock(0); 
        productRepository.save(product);
    }
	public List<String> getImageUrlsByProductId(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateProductImages(Integer id, List<String> urlsToAdd) {
		// TODO Auto-generated method stub
		
	}

}
