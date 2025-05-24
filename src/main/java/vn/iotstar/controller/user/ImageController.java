package vn.iotstar.controller.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource; // Sử dụng Resource thay vì InputStreamResource cho FileSystemResource
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files; // Thêm Files để probeContentType
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.InvalidPathException; // Thêm InvalidPathException

// API Truy cập ảnh đã lưu
@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${app.upload.directory}")
    private String uploadDir;

    @GetMapping("")
    public ResponseEntity<?> getImage(@RequestParam String imageName) {

        // Bỏ đoạn mã xử lý URL ngoài (SSRF) hoàn toàn.
        // Endpoint này chỉ phục vụ ảnh từ thư mục upload nội bộ.

        if (imageName == null || imageName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Tên ảnh không được để trống.");
        }

        try {
            // 1. Chuẩn hóa và phân giải đường dẫn thư mục upload
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

            // 2. Xây dựng đường dẫn đầy đủ đến tệp ảnh yêu cầu
            // Sử dụng resolve để an toàn hơn nối chuỗi
            Path requestedFilePath = Paths.get(uploadDir).resolve(imageName).toAbsolutePath().normalize();

            // 3. Kiểm tra Path Traversal: Đảm bảo đường dẫn yêu cầu nằm trong thư mục upload
            // Nếu đường dẫn yêu cầu không bắt đầu bằng đường dẫn thư mục upload, đó là tấn công Path Traversal
            if (!requestedFilePath.startsWith(uploadPath)) {
                System.err.println("Attempted path traversal: " + imageName);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Truy cập bị cấm."); // Trả về 403 Forbidden
            }

            File imageFile = requestedFilePath.toFile();

            // 4. Kiểm tra file có tồn tại không
            if (imageFile.exists() && imageFile.isFile()) { // Thêm kiểm tra là file
                Resource resource = new FileSystemResource(imageFile);

                // 5. Xác định MediaType dựa trên nội dung hoặc tên tệp (an toàn hơn)
                String contentType = Files.probeContentType(imageFile.toPath());
                if (contentType == null) {
                    // Mặc định nếu không xác định được
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Hoặc một loại mặc định an toàn
                }

                // Kiểm tra nếu content type không phải là ảnh, có thể từ chối hoặc trả về loại an toàn
                if (!contentType.startsWith("image/")) {
                    // Đây có thể là một tệp độc hại được upload với tên ảnh
                    // Tùy thuộc vào yêu cầu bảo mật, bạn có thể từ chối phục vụ hoặc phục vụ dưới dạng octet-stream
                    System.err.println("Attempted to serve non-image file as image: " + imageName + " with content type: " + contentType);
                    // return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Loại tệp không hợp lệ."); // Tùy chọn từ chối
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Hoặc phục vụ dưới dạng binary để ngăn chặn thực thi
                }


                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType)) // Sử dụng MediaType đã xác định
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageFile.getName() + "\"") // Gợi ý hiển thị inline
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build(); // Trả về 404 nếu file không tồn tại hoặc không phải file
            }
        } catch (InvalidPathException e) {
             // Xử lý lỗi nếu imageName chứa ký tự không hợp lệ tạo thành đường dẫn không hợp lệ
            System.err.println("Invalid path in image request: " + imageName + " - " + e.getMessage());
            return ResponseEntity.badRequest().body("Tên ảnh không hợp lệ.");
        }
        catch (Exception e) {
            System.err.println("Lỗi khi truy xuất ảnh: " + imageName + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi truy xuất ảnh."); // Trả về 500 cho lỗi nội bộ
        }
    }
}