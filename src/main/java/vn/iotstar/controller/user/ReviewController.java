package vn.iotstar.controller.user;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.InvalidPathException; // Import this
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional; // Import this
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
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
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.dto.ReviewDTO;
import vn.iotstar.entity.Person;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.ProductFeedback;
//import vn.iotstar.entity.User; // User entity might not be needed directly here if Person is used
import vn.iotstar.service.IProductFeedbackService;
import vn.iotstar.service.IProductService;

@Controller
@RequestMapping("/User/review")
public class ReviewController {

	@Value("${app.upload.directory}")
	private String uploadDirectory;

	@Value("${app.upload.max-file-size:5MB}") // Configure max file size (e.g., 5MB)
    private String maxFileSize;

	@Autowired
	private IProductService productService;
	@Autowired
	private IProductFeedbackService feedbackService;

    // Helper method to parse file size string (e.g., "5MB", "10KB")
	private long parseMaxFileSize(String size) {
        size = size.toUpperCase();
        if (size.endsWith("MB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024;
        } else if (size.endsWith("KB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024;
        } else {
            // Consider it bytes if no unit, but ideally units should be used
            return Long.parseLong(size);
        }
    }


	@PostMapping("")
	public String review(ModelMap model, HttpSession session, @ModelAttribute ReviewDTO review,
			             @RequestParam("productId") int productId, @RequestParam("orderId") int orderId,
                         RedirectAttributes redirectAttributes) {

		Person user = (Person) session.getAttribute("user");
        // Basic check if user is logged in (assuming 'Person' is your user entity)
        if (user == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để viết đánh giá.");
             return "redirect:/login"; // Redirect to login page if not logged in
        }

		String storedFileName = null;
		MultipartFile imageFile = review.getImage();

        // Process image file if provided and not empty
        if (imageFile != null && !imageFile.isEmpty()) {
            Path filePath = null; // Define filePath here to use it in cleanup
            try {
                // --- File Validation ---
                // 1. Check file size
                long maxSizeBytes = parseMaxFileSize(maxFileSize);
                if (imageFile.getSize() > maxSizeBytes) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Kích thước tệp ảnh vượt quá giới hạn cho phép (" + maxFileSize + ").");
                    return "redirect:/User/Order/detail/" + orderId;
                }

                // 2. Check file type (only allow images) - Initial check using MIME type from browser
                String contentType = imageFile.getContentType();
                if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png") && !contentType.startsWith("image/gif") && !contentType.startsWith("image/webp"))) { // Added webp
                    redirectAttributes.addFlashAttribute("errorMessage", "Loại tệp không được hỗ trợ. Chỉ chấp nhận JPEG, PNG, GIF, WEBP.");
                    return "redirect:/User/Order/detail/" + orderId;
                }
                 // --- End File Validation ---

                // --- Safe File Naming and Saving ---
                // 3. Create a unique and safe filename
                String originalFilename = imageFile.getOriginalFilename();
                // Get file extension safely using Apache Commons IO
                String fileExtension = FilenameUtils.getExtension(originalFilename);
                 if (fileExtension == null || fileExtension.trim().isEmpty()) {
                      // Reject files without extension or just an invalid extension might be safer
                       System.err.println("Upload rejected: File has no extension - " + originalFilename);
                       redirectAttributes.addFlashAttribute("errorMessage", "Tệp ảnh phải có phần mở rộng (ví dụ: .jpg, .png).");
                       return "redirect:/User/Order/detail/" + orderId;
                 }
                // Ensure extension is lowercase for consistency
                fileExtension = fileExtension.toLowerCase();
                 // Basic check for common image extensions
                 if (!Arrays.asList("jpg", "jpeg", "png", "gif", "webp").contains(fileExtension)) {
                      System.err.println("Upload rejected: Invalid extension - " + originalFilename);
                      redirectAttributes.addFlashAttribute("errorMessage", "Phần mở rộng tệp không hợp lệ. Chỉ chấp nhận .jpg, .png, .gif, .webp.");
                      return "redirect:/User/Order/detail/" + orderId;
                 }


                storedFileName = UUID.randomUUID().toString() + "." + fileExtension; // Append safe extension

                // 4. Prepare upload directory and file path safely
                Path uploadPath = Paths.get(uploadDirectory).toAbsolutePath().normalize();
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                // Resolve the full path safely using Path.resolve and normalize
                filePath = uploadPath.resolve(storedFileName).normalize();

                // 5. Extra safety check: Ensure the resolved path is still within the intended upload directory
                if (!filePath.startsWith(uploadPath)) {
                     System.err.println("Attempted path traversal during review image upload: " + originalFilename);
                     redirectAttributes.addFlashAttribute("errorMessage", "Đường dẫn tệp không hợp lệ.");
                     // No file was saved at this point, so no cleanup needed yet
                     return "redirect:/User/Order/detail/" + orderId;
                }


                // 6. Save the file to the filesystem
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // 7. *** SERVER-SIDE CONTENT TYPE PROBE *** (Crucial check)
                // After saving, probe the actual content to confirm it's an image
                String probedContentType = Files.probeContentType(filePath);
                 if (probedContentType == null || !probedContentType.startsWith("image/")) {
                     // The file content is not an image despite the extension/MIME type from the browser
                     System.err.println("Attempted to upload non-image content: " + originalFilename + " (Probed type: " + probedContentType + "). File deleted.");
                     Files.deleteIfExists(filePath); // Delete the potentially malicious file from filesystem
                     redirectAttributes.addFlashAttribute("errorMessage", "Nội dung tệp không phải là ảnh hợp lệ.");
                     return "redirect:/User/Order/detail/" + orderId; // Stop processing and show error
                 }
                // --- End Safe File Naming and Saving ---


            } catch (InvalidPathException e) {
                 // Handle cases where originalFilename might contain invalid characters
                System.err.println("Invalid path construction during upload for file: " + (imageFile != null ? imageFile.getOriginalFilename() : "N/A") + " - " + e.getMessage());
                 // Attempt cleanup if filePath was partially constructed
                 if (filePath != null) {
                     try { Files.deleteIfExists(filePath); } catch (IOException cleanEx) { System.err.println("Failed to clean up partial path error: " + cleanEx.getMessage());}
                 }
                redirectAttributes.addFlashAttribute("errorMessage", "Tên tệp ảnh không hợp lệ.");
                return "redirect:/User/Order/detail/" + orderId;
            }
            catch (IOException ex) {
                System.err.println("Lỗi xử lý tệp khi tải ảnh lên đánh giá: " + ex.getMessage());
                 // Attempt cleanup if filePath was partially constructed
                 if (filePath != null) {
                     try { Files.deleteIfExists(filePath); } catch (IOException cleanEx) { System.err.println("Failed to clean up IO error: " + cleanEx.getMessage());}
                 }
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi tải ảnh lên. Vui lòng thử lại.");
                return "redirect:/User/Order/detail/" + orderId;
            }
            catch (Exception ex) {
                // Catch any other unexpected exceptions during file processing
                 System.err.println("Lỗi không xác định khi xử lý ảnh đánh giá: " + ex.getMessage());
                 // Attempt cleanup if filePath was partially constructed
                 if (filePath != null) {
                     try { Files.deleteIfExists(filePath); } catch (IOException cleanEx) { System.err.println("Failed to clean up unknown error: " + cleanEx.getMessage());}
                 }
                redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý ảnh. Vui lòng thử lại.");
                return "redirect:/User/Order/detail/" + orderId;
            }
        } // End if imageFile is not empty

        // --- Proceed to save feedback only if file processing was successful (or no file was uploaded) ---

		ProductFeedback feedback = new ProductFeedback();
		BeanUtils.copyProperties(review, feedback);
		feedback.setImage(storedFileName); // storedFileName will be null if no file was uploaded or if upload failed after validation

		Optional<Product> productOptional = productService.findById(productId);
        if (!productOptional.isPresent()) {
            // Handle case where product ID is invalid (should ideally be validated earlier)
            System.err.println("Product not found for review: ID " + productId);
            redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại.");
             // Clean up the uploaded file if product is not found (only if storedFileName is not null)
             if (storedFileName != null) {
                 try {
                      Path filePath = Paths.get(uploadDirectory).toAbsolutePath().normalize().resolve(storedFileName).normalize();
                      Files.deleteIfExists(filePath);
                 } catch (Exception e) { // Catch Exception as path resolution might fail
                      System.err.println("Failed to clean up file after product not found error: " + storedFileName + " - " + e.getMessage());
                 }
             }
            return "redirect:/User/Order/detail/" + orderId;
        }
		Product product = productOptional.get();

		feedback.setProduct(product);
		feedback.setUser(user); // Assign the logged-in user (Person entity)

        try {
		    feedbackService.save(feedback);
             redirectAttributes.addFlashAttribute("successMessage", "Đánh giá của bạn đã được gửi thành công!");
        } catch (Exception e) {
             System.err.println("Lỗi khi lưu đánh giá vào DB: " + e.getMessage());
             redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi lưu đánh giá. Vui lòng thử lại.");
             // Clean up the uploaded file if DB save fails (only if storedFileName is not null)
             if (storedFileName != null) {
                 try {
                      Path filePath = Paths.get(uploadDirectory).toAbsolutePath().normalize().resolve(storedFileName).normalize();
                      Files.deleteIfExists(filePath);
                 } catch (Exception ioException) { // Catch Exception as path resolution might fail
                      System.err.println("Failed to clean up file after DB error: " + storedFileName + " - " + ioException.getMessage());
                 }
             }
        }


		return "redirect:/User/Order/detail/" + orderId; // Redirect back to order detail

	}

	@GetMapping("") // Consider changing this mapping, it's unconventional for listing feedbacks
	public ResponseEntity<List<ProductFeedback>> getFeedbacks(@RequestParam("productId") int productId,
	                                                           @RequestParam(defaultValue = "0") int page, // Add default value
                                                               @RequestParam(defaultValue = "10") int size) { // Add default value
	    Page<ProductFeedback> feedbackPage = feedbackService.findByProduct_ProductId(productId, PageRequest.of(page, size));
	    return ResponseEntity.ok(feedbackPage.getContent());
	}
}