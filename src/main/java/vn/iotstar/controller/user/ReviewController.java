package vn.iotstar.controller.user;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.commons.io.FilenameUtils; // Thêm dependency commons-io để lấy đuôi tệp
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType; // Để kiểm tra MIME type
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult; // Thêm BindingResult để xử lý lỗi validation
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	
	@Value("${app.upload.max-file-size:5MB}") // Cấu hình kích thước tối đa (ví dụ: 5MB)
    private String maxFileSize; 
	
	@Autowired
	private IProductService productService;
	@Autowired
	private IProductFeedbackService feedbackService;
	@PostMapping("")
	public String review(ModelMap model, HttpSession session, @ModelAttribute ReviewDTO review
			,@RequestParam("productId") int productId,@RequestParam("orderId") int orderId,
            RedirectAttributes redirectAttributes) {

		Person user = (Person) session.getAttribute("user");
		String storedFileName = null;
		MultipartFile imageFile = review.getImage();
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // 2a. Kiểm tra kích thước tệp
                long maxSizeBytes = parseMaxFileSize(maxFileSize); // Chuyển đổi từ String sang byte
                if (imageFile.getSize() > maxSizeBytes) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Kích thước tệp ảnh vượt quá giới hạn cho phép (" + maxFileSize + ").");
                    return "redirect:/User/Order/detail/" + orderId;
                }

                // 2b. Kiểm tra loại tệp (chỉ cho phép ảnh)
                String contentType = imageFile.getContentType();
                if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png") && !contentType.startsWith("image/gif"))) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Loại tệp không được hỗ trợ. Chỉ chấp nhận JPEG, PNG, GIF.");
                    return "redirect:/User/Order/detail/" + orderId;
                }

                // 2c. Tạo tên tệp duy nhất và an toàn
                String originalFilename = imageFile.getOriginalFilename();
                String fileExtension = FilenameUtils.getExtension(originalFilename); // Lấy đuôi tệp an toàn
                storedFileName = UUID.randomUUID().toString() + (fileExtension != null && !fileExtension.isEmpty() ? "." + fileExtension : "");

                // 2d. Lưu tệp vào thư mục
                Path uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize(); // Chuẩn hóa đường dẫn
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(storedFileName);

                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

            } catch (Exception ex) {
                System.err.println("Lỗi khi tải ảnh lên: " + ex.getMessage()); // Sử dụng err cho lỗi
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải ảnh lên. Vui lòng thử lại.");
                return "redirect:/User/Order/detail/" + orderId;
            }
        }
		
		ProductFeedback feedback = new ProductFeedback();
		BeanUtils.copyProperties(review, feedback);
		feedback.setImage(storedFileName);
		feedback.setReviewDate(LocalDateTime.now());
		
		Product product = productService.findById(productId).get();
		
		feedback.setProduct(product);
		feedback.setUser(user);
		feedbackService.save(feedback);

		return "redirect:/User/Order/detail/" + orderId;

	}
	
	private long parseMaxFileSize(String size) {
        size = size.toUpperCase();
        if (size.endsWith("MB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024;
        } else if (size.endsWith("KB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024;
        } else {
            return Long.parseLong(size); // Coi là byte
        }
    }
	@GetMapping("")
	public ResponseEntity<List<ProductFeedback>> getFeedbacks(@RequestParam("productId") int productId,
	                                                           @RequestParam("page") int page, @RequestParam("size") int size) {
	    Page<ProductFeedback> feedbackPage = feedbackService.findByProduct_ProductId(productId, PageRequest.of(page, size));
	    return ResponseEntity.ok(feedbackPage.getContent());
	}
}
