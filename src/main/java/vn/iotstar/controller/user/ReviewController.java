package vn.iotstar.controller.user;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.dto.ReviewDTO;
import vn.iotstar.entity.Person;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.ProductFeedback;
import vn.iotstar.entity.User;
import vn.iotstar.service.IProductFeedbackService;
import vn.iotstar.service.IProductService;

@Controller
@RequestMapping("/User/review")
public class ReviewController {
	@Value("${app.upload.directory}")
	private String uploadDirectory;
	@Autowired
	private IProductService productService;
	@Autowired
	private IProductFeedbackService feedbackService;
	@PostMapping("")
	public String review(ModelMap model, HttpSession session, @ModelAttribute ReviewDTO review
			,@RequestParam("productId") int productId,@RequestParam("orderId") int orderId) {

		Person user = (Person) session.getAttribute("user");

		// save image file
		MultipartFile image = review.getImage();
		String randString = RandomStringUtils.randomAlphanumeric(10);
		String storageFileName = randString + "_" + image.getOriginalFilename();

		try {
			Path uploadPath = Paths.get(uploadDirectory);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
				System.out.println("Directory created: " + uploadPath);
			}

			try (InputStream inputStream = image.getInputStream()) {
				Files.copy(inputStream, uploadPath.resolve(storageFileName), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
		}
		
		ProductFeedback feedback = new ProductFeedback();
		BeanUtils.copyProperties(review, feedback);
		feedback.setImage(storageFileName);
		feedback.setReviewDate(LocalDateTime.now());
		
		Product product = productService.findById(productId).get();
		
		feedback.setProduct(product);
		feedback.setUser(user);
		feedbackService.save(feedback);

		return "redirect:/User/Order/detail/" + orderId;

	}
	@GetMapping("")
	public ResponseEntity<List<ProductFeedback>> getFeedbacks(@RequestParam("productId") int productId,
	                                                           @RequestParam("page") int page, @RequestParam("size") int size) {
	    Page<ProductFeedback> feedbackPage = feedbackService.findByProduct_ProductId(productId, PageRequest.of(page, size));
	    return ResponseEntity.ok(feedbackPage.getContent());
	}
}
