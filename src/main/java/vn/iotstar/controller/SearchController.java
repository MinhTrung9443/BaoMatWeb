package vn.iotstar.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.ProductFeedback;
import vn.iotstar.entity.Voucher;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductFeedbackService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IVoucherService;

@Controller
public class SearchController {

	@Autowired
	private IProductService productService;
	@Autowired
	private ICategoryService categoryService;
	@Autowired
	private IVoucherService voucherService;

	// Lọc sản phẩm theo các điều kiện
	@GetMapping({"/search"})
	public String listProducts(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "minPrice", required = false) Integer minPrice,
			@RequestParam(value = "maxPrice", required = false) Integer maxPrice,
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "category_name", required = false) String categoryName,
			@RequestParam(value = "brandOrigin", required = false) String brandOrigin,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "21") int size, Model model) {

		// Đảm bảo rằng các giá trị phân trang hợp lệ
		Pageable pageable = PageRequest.of(page, size);

		// Xử lý tìm kiếm và phân trang
		Page<Product> productPage = Page.empty();
		;
		if (keyword != null && !keyword.isEmpty()) {
			// Nếu có keyword, tìm kiếm sản phẩm theo tên
			productPage = productService.getProductsByName(keyword, pageable);
		} else {
			// Trường hợp không có bộ lọc nào
			if (minPrice == null && maxPrice == null && (brand == null || brand.isEmpty())
					&& (brandOrigin == null || brandOrigin.isEmpty())
					&& (categoryName == null || categoryName.isEmpty())) {
				productPage = productService.getAllProducts(pageable); // Lấy tất cả sản phẩm nếu không có bộ lọc
			} else {
				// Trường hợp có bộ lọc
				if (minPrice != null && maxPrice == null) {
					productPage = productService.getProductsByPriceRange(minPrice, Integer.MAX_VALUE, pageable);
					System.out.println("Filtered by min price: " + minPrice);
				} else if (minPrice == null && maxPrice != null) {
					productPage = productService.getProductsByPriceRange(0, maxPrice, pageable);
				} else if (minPrice != null && maxPrice != null) {
					productPage = productService.getProductsByPriceRange(minPrice, maxPrice, pageable); // Bộ lọc theo
																										// giá
				} else if ((brand != null && !brand.isEmpty())) {
					productPage = productService.getProductsByBrand(brand, pageable); // Bộ lọc theo thương hiệu
				} else if (brandOrigin != null && !brandOrigin.isEmpty()) {
					productPage = productService.getProductsByBrandOrigin(brandOrigin, pageable); // Bộ lọc theo nguồn
																									// gốc thương hiệu
				} else if (categoryName != null && !categoryName.isEmpty()) {
					productPage = productService.getProductsByCategoryName(categoryName, pageable); // Bộ lọc theo danh
																									// mục
				}
			}
		}
		List<String> brands = productService.findByBrand(); // Phương thức để lấy danh sách từ DB
		model.addAttribute("brands", brands);
		List<String> originBrand = productService.findByOriginBrand(); // Phương thức để lấy danh sách từ DB
		model.addAttribute("brandOrigins", originBrand);
		List<String> categoryname1 = productService.findByCategoryName(); // Phương thức để lấy danh sách từ DB
		model.addAttribute("category_names", categoryname1);

		// Thêm dữ liệu vào model để hiển thị trong view
		model.addAttribute("products", productPage.getContent());
		model.addAttribute("minPrice", minPrice);
		model.addAttribute("maxPrice", maxPrice);
		model.addAttribute("brand", brand);
		model.addAttribute("category_name", categoryName);
		model.addAttribute("brandOrigin", brandOrigin);

		// Thêm phân trang vào model
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", productPage.getTotalPages());
		model.addAttribute("totalItems", productPage.getTotalElements());

		return "/product-search"; // Trả về trang HTML với các sản phẩm đã phân trang
	}

	// Lọc sản phẩm theo các điều kiện
	@GetMapping({ "/User/Search" })
	public String UserlistProducts(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "minPrice", required = false) Integer minPrice,
			@RequestParam(value = "maxPrice", required = false) Integer maxPrice,
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "category_name", required = false) String categoryName,
			@RequestParam(value = "brandOrigin", required = false) String brandOrigin,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "21") int size, Model model) {

		// Đảm bảo rằng các giá trị phân trang hợp lệ
		Pageable pageable = PageRequest.of(page, size);

		// Xử lý tìm kiếm và phân trang
		Page<Product> productPage = Page.empty();
		;
		if (keyword != null && !keyword.isEmpty()) {
			// Nếu có keyword, tìm kiếm sản phẩm theo tên
			productPage = productService.getProductsByName(keyword, pageable);
		} else {
			// Trường hợp không có bộ lọc nào
			if (minPrice == null && maxPrice == null && (brand == null || brand.isEmpty())
					&& (brandOrigin == null || brandOrigin.isEmpty())
					&& (categoryName == null || categoryName.isEmpty())) {
				productPage = productService.getAllProducts(pageable); // Lấy tất cả sản phẩm nếu không có bộ lọc
			} else {
				// Trường hợp có bộ lọc
				if (minPrice != null && maxPrice == null) {
					productPage = productService.getProductsByPriceRange(minPrice, Integer.MAX_VALUE, pageable);
					System.out.println("Filtered by min price: " + minPrice);
				} else if (minPrice == null && maxPrice != null) {
					productPage = productService.getProductsByPriceRange(0, maxPrice, pageable);
				} else if (minPrice != null && maxPrice != null) {
					productPage = productService.getProductsByPriceRange(minPrice, maxPrice, pageable); // Bộ lọc theo
																										// giá
				} else if ((brand != null && !brand.isEmpty())) {
					productPage = productService.getProductsByBrand(brand, pageable); // Bộ lọc theo thương hiệu
				} else if (brandOrigin != null && !brandOrigin.isEmpty()) {
					productPage = productService.getProductsByBrandOrigin(brandOrigin, pageable); // Bộ lọc theo nguồn
																									// gốc thương hiệu
				} else if (categoryName != null && !categoryName.isEmpty()) {
					productPage = productService.getProductsByCategoryName(categoryName, pageable); // Bộ lọc theo danh
																									// mục
				}
			}
		}
		List<String> brands = productService.findByBrand(); // Phương thức để lấy danh sách từ DB
		model.addAttribute("brands", brands);
		List<String> originBrand = productService.findByOriginBrand(); // Phương thức để lấy danh sách từ DB
		model.addAttribute("brandOrigins", originBrand);
		List<String> categoryname1 = productService.findByCategoryName(); // Phương thức để lấy danh sách từ DB
		model.addAttribute("category_names", categoryname1);

		// Thêm dữ liệu vào model để hiển thị trong view
		model.addAttribute("products", productPage.getContent());
		model.addAttribute("minPrice", minPrice);
		model.addAttribute("maxPrice", maxPrice);
		model.addAttribute("brand", brand);
		model.addAttribute("category_name", categoryName);
		model.addAttribute("brandOrigin", brandOrigin);

		// Thêm phân trang vào model
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", productPage.getTotalPages());
		model.addAttribute("totalItems", productPage.getTotalElements());

		return "User/product-search-user"; // Trả về trang HTML với các sản phẩm đã phân trang
	}
	@GetMapping({ "/Shipper/Search","/Admin/Search","/Vendor/Search" })
	public String vendorlistProducts(@RequestParam(value = "keyword", required = false) String keyword,
			@RequestParam(value = "minPrice", required = false) Integer minPrice,
			@RequestParam(value = "maxPrice", required = false) Integer maxPrice,
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "category_name", required = false) String categoryName,
			@RequestParam(value = "brandOrigin", required = false) String brandOrigin,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "21") int size, Model model) {

		// Đảm bảo rằng các giá trị phân trang hợp lệ
		Pageable pageable = PageRequest.of(page, size);

		// Xử lý tìm kiếm và phân trang
		Page<Product> productPage = Page.empty();
		;
		if (keyword != null && !keyword.isEmpty()) {
			// Nếu có keyword, tìm kiếm sản phẩm theo tên
			productPage = productService.getProductsByName(keyword, pageable);
		} else {
			// Trường hợp không có bộ lọc nào
			if (minPrice == null && maxPrice == null && (brand == null || brand.isEmpty())
					&& (brandOrigin == null || brandOrigin.isEmpty())
					&& (categoryName == null || categoryName.isEmpty())) {
				productPage = productService.getAllProducts(pageable); // Lấy tất cả sản phẩm nếu không có bộ lọc
			} else {
				// Trường hợp có bộ lọc
				if (minPrice != null && maxPrice == null) {
					productPage = productService.getProductsByPriceRange(minPrice, Integer.MAX_VALUE, pageable);
					System.out.println("Filtered by min price: " + minPrice);
				} else if (minPrice == null && maxPrice != null) {
					productPage = productService.getProductsByPriceRange(0, maxPrice, pageable);
				} else if (minPrice != null && maxPrice != null) {
					productPage = productService.getProductsByPriceRange(minPrice, maxPrice, pageable); // Bộ lọc theo
																										// giá
				} else if ((brand != null && !brand.isEmpty())) {
					productPage = productService.getProductsByBrand(brand, pageable); // Bộ lọc theo thương hiệu
				} else if (brandOrigin != null && !brandOrigin.isEmpty()) {
					productPage = productService.getProductsByBrandOrigin(brandOrigin, pageable); // Bộ lọc theo nguồn
																									// gốc thương hiệu
				} else if (categoryName != null && !categoryName.isEmpty()) {
					productPage = productService.getProductsByCategoryName(categoryName, pageable); // Bộ lọc theo danh
																									// mục
				}
			}
		}
		List<String> brands = productService.findByBrand(); // Phương thức để lấy danh sách từ DB
		model.addAttribute("brands", brands);
		List<String> originBrand = productService.findByOriginBrand(); // Phương thức để lấy danh sách từ DB
		model.addAttribute("brandOrigins", originBrand);
		List<String> categoryname1 = productService.findByCategoryName(); // Phương thức để lấy danh sách từ DB
		model.addAttribute("category_names", categoryname1);

		// Thêm dữ liệu vào model để hiển thị trong view
		model.addAttribute("products", productPage.getContent());
		model.addAttribute("minPrice", minPrice);
		model.addAttribute("maxPrice", maxPrice);
		model.addAttribute("brand", brand);
		model.addAttribute("category_name", categoryName);
		model.addAttribute("brandOrigin", brandOrigin);

		// Thêm phân trang vào model
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", productPage.getTotalPages());
		model.addAttribute("totalItems", productPage.getTotalElements());

		return "/product-search"; // Trả về trang HTML với các sản phẩm đã phân trang
	}

	@Autowired
	private IProductFeedbackService feedbackService;

	// Xử lý yêu cầu hiển thị chi tiết sản phẩm
	@GetMapping("/product-details/{productId}")
	public String showProductDetails(@PathVariable("productId") int productId, Model model) {
		Optional<Product> opProduct = productService.findById(productId);

		if (opProduct.isPresent()) {
			Product product = opProduct.get();
			List<ProductFeedback> feedback = feedbackService.findByProduct_ProductId(productId);
			Double rating = feedbackService.findAverageRatingByProductId((long) productId);
			double finalRating = (rating != null) ? rating : 0.0;
			Integer review = product.getFeedbacks().size();
			int numReview = (review != null) ? review : 0;
			model.addAttribute("numReview", numReview);
			model.addAttribute("rating", finalRating);
			model.addAttribute("product", product);
			model.addAttribute("feedback", feedback);


			List<Product> top5 = productService.findTop5ByFavouriteCount(product.getCategory().getCategoryId());
			Map<Product, Double> top5Product = new HashMap<>();
			for (Product pro : top5)
			{
				Double rate = feedbackService.findAverageRatingByProductId((long) productId);
				double rate2 = (rate != null) ? rating : 0.0;
				top5Product.put(pro, rate2);
			}
			model.addAttribute("top5", top5Product);
			Voucher voucher = voucherService.findRandomValidActiveVoucher();
			
			model.addAttribute("voucher", voucher);

			return "product-details"; // Tên của trang HTML chi tiết sản phẩm (product-details.html)
		}
		return "product-details"; // Tên của trang HTML chi tiết sản phẩm (product-details.html)
	}
}
