package vn.iotstar.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Account;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;

@Controller
@RequestMapping("/")
public class HomeController {
	@Autowired
	private IProductService productService; // Inject service lấy dữ liệu
	@Autowired
	private ICategoryService categoryService;

	@GetMapping("")
	public String showHomePage(Model model, HttpSession session) {
		
		// Lấy tất cả sản phẩm từ database
		List<Product> topSellingProducts = productService.getTop10BestSellingProducts();
        model.addAttribute("products", topSellingProducts);
        
        List<Category> listCate = categoryService.findAll();
        session.setAttribute("listCate", listCate);

		// Trả về tên của trang HTML để Thymeleaf render (ví dụ: guest/index.html)
		return "Guest/index";
	}
}
