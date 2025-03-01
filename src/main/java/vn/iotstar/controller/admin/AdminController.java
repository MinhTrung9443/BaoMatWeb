package vn.iotstar.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.service.IOrderLineService;
import vn.iotstar.service.IProductFeedbackService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IUserService;

@Controller
@RequestMapping("/Admin")
public class AdminController {

	@Autowired
	private IUserService userService;

	@Autowired
	private IProductService productService;

	@Autowired
	private IOrderLineService orderLineService;
	
	@Autowired IProductFeedbackService feedbackService;

	@GetMapping("")
	public String home(Model model) {
		// Lấy danh sách các sản phẩm bán chạy nhất
		List<Product> topSellingProducts = productService.getTop10BestSellingProducts();
		Map<Product, Integer> top10Product = new HashMap<>();
		for (Product pro : topSellingProducts) {
			Integer topSell = orderLineService.findTotalQuantityByProductId((Integer) pro.getProductId());
			top10Product.put(pro, (topSell != null) ? topSell : 0);
		}
		List<Map.Entry<Product, Integer>> sortedList = new ArrayList<>(top10Product.entrySet());
		sortedList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue())); // Sắp xếp giảm dần
		model.addAttribute("topProduct", sortedList);
		

		List<Product> top20 = productService.findTop20ByFavouriteCount();
		Map<Product, Double> top20Product = new HashMap<>();
		
		for (Product pro : top20) {
			Double rating = feedbackService.findAverageRatingByProductId((long) pro.getProductId());
		
			double rate2 = (rating != null) ? rating : 0.0;
			top20Product.put(pro, rate2);
		}
		List<Map.Entry<Product, Double>> sortedListFavor = new ArrayList<>(top20Product.entrySet());
		sortedListFavor.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));  // Sắp xếp giảm dần theo rating

	    // Lấy 20 sản phẩm đầu tiên (sau khi đã sắp xếp)
	    List<Map.Entry<Product, Double>> top20Sorted = sortedListFavor.stream().limit(20).collect(Collectors.toList());
		model.addAttribute("topFavourite", top20Sorted);

		return "Admin/home";
	}

	@GetMapping("/search")
	public String search(Model model, @RequestParam(value = "fullname", required = false) String fullname) {

		List<User> list = new ArrayList<>();
		if (StringUtils.hasText(fullname)) {
			List<User> user = userService.findByFullnameContaining(fullname);
			if (!(user.size() == 0)){
				model.addAttribute("message", "KHÔNG CÓ KẾT QUẢ NÀO ĐƯỢC TÌM THẤY");
			}
		}
		model.addAttribute("list", list);
		return "Admin/search";
	}

}
