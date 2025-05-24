package vn.iotstar.controller.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

// API Truy cập ảnh đã lưu
@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Value("${app.upload.directory}")
    private String uploadDir;

    @GetMapping("")
    public ResponseEntity<?> getImage(@RequestParam String imageName) {
//        neu la url thi dung nhu nay
        if (imageName.startsWith("http://") || imageName.startsWith("https://")) {
            // Nếu URL hợp lệ, trả về ảnh từ URL ngoài
            try {
                URL url = new URL(imageName);  // Kiểm tra tính hợp lệ của URL
                InputStream inputStream = url.openStream();  // Tải ảnh từ URL
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)  // Hoặc MediaType.IMAGE_PNG tùy thuộc vào loại ảnh
                        .body(new InputStreamResource(inputStream));  // Trả về ảnh dưới dạng InputStream
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Error fetching image from URL: " + e.getMessage());
            }
        }

        try {
            // Tạo file từ tên ảnh
            File imageFile = new File(uploadDir + File.separator + imageName);
            
            // Kiểm tra file có tồn tại không
            if (imageFile.exists()) {
                // Trả về file ảnh
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new FileSystemResource(imageFile));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving image: " + e.getMessage());
        }
    }
}