package vn.iotstar.controller.vendor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import vn.iotstar.dto.ProductRequestDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.service.impl.CategoryService;
import vn.iotstar.service.impl.ProductService;

@Controller
@RequestMapping("/Vendor/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Value("${app.upload.directory}")
    private String uploadDir;
    
    @Value("${app.upload.max-file-size:5MB}") // Configure max file size, default 5MB
    private String maxFileSize;
    
    private long parseMaxFileSize(String size) {
        size = size.toUpperCase();
        if (size.endsWith("MB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024 * 1024;
        } else if (size.endsWith("KB")) {
            return Long.parseLong(size.substring(0, size.length() - 2)) * 1024;
        } else {
            return Long.parseLong(size); // Assume bytes if no unit
        }
    }

    @GetMapping("")
    public String listProducts(Model model, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("warehouseDateFirst")));
        Page<Product> productPage;
        if (search != null && !search.isEmpty()) {
            productPage = productService.searchProducts(search, pageable);
            model.addAttribute("searchTerm", search); // Để giữ lại giá trị tìm kiếm trong ô nhập liệu
        } else {
            productPage = productService.findAllAvailable(pageable);
        }
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        return "Vendor/product_list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {       
    	List<Category> activeCategories = categoryService.getActiveCategories();
        model.addAttribute("productRequestDTO", new ProductRequestDTO());
        model.addAttribute("categories", activeCategories);
        return "Vendor/product/create"; // Tên file Thymeleaf
    }

    @PostMapping("/create")
    public String createProduct(ProductRequestDTO productRequestDTO, Model model, @RequestParam("files") MultipartFile[] files) {
    	if (productRequestDTO.getManufactureDate().isAfter(productRequestDTO.getExpirationDate())) {
            model.addAttribute("errorMessage", "Ngày sản xuất phải trước ngày hết hạn.");
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "Vendor/product/create";
        }
        if (productRequestDTO.getWarehouse_date_first().isBefore(productRequestDTO.getManufactureDate()) || 
            productRequestDTO.getWarehouse_date_first().isAfter(productRequestDTO.getExpirationDate())) {
            model.addAttribute("errorMessage", "Ngày nhập kho phải nằm giữa ngày sản xuất và ngày hết hạn.");
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "Vendor/product/create";
        }
        
        List<String> imageUrls = new ArrayList<>();
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize(); // Normalize upload directory path
        long maxSizeBytes = parseMaxFileSize(maxFileSize);

        try {
            // Ensure upload directory exists
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue; // Skip empty files
                }

                // --- File Validation ---
                // 1. Size Check
                if (file.getSize() > maxSizeBytes) {
                    model.addAttribute("errorMessage", "Một hoặc nhiều tệp vượt quá kích thước tối đa (" + maxFileSize + ").");
                    // Clean up any files already uploaded in this batch (optional but good)
                    cleanupUploadedFiles(imageUrls, uploadPath);
                    model.addAttribute("categories", categoryService.getActiveCategories());
                     model.addAttribute("productRequestDTO", productRequestDTO); // Keep form data
                    return "Vendor/product/create";
                }

                // 2. Initial Type Check (MIME from browser)
                String contentType = file.getContentType();
                if (contentType == null || (!contentType.startsWith("image/jpeg") && !contentType.startsWith("image/png") && !contentType.startsWith("image/gif"))) {
                     model.addAttribute("errorMessage", "Chỉ chấp nhận các tệp ảnh (JPEG, PNG, GIF). Tệp '" + file.getOriginalFilename() + "' có định dạng không hợp lệ.");
                     cleanupUploadedFiles(imageUrls, uploadPath);
                     model.addAttribute("categories", categoryService.getActiveCategories());
                      model.addAttribute("productRequestDTO", productRequestDTO); // Keep form data
                     return "Vendor/product/create";
                }
                // --- End File Validation ---


                // --- Safe File Naming and Saving ---
                String originalFilename = file.getOriginalFilename();
                String fileExtension = FilenameUtils.getExtension(originalFilename); // Get extension safely
                // Generate a unique filename using UUID + safe extension
                String storedFileName = UUID.randomUUID().toString() + (fileExtension != null && !fileExtension.isEmpty() ? "." + fileExtension : "");

                // Resolve the full path safely
                Path filePath = uploadPath.resolve(storedFileName).normalize(); // Resolve and normalize final file path

                 // Ensure the resolved path is still within the intended upload directory (extra safety)
                if (!filePath.startsWith(uploadPath)) {
                     model.addAttribute("errorMessage", "Đường dẫn tệp không hợp lệ.");
                     cleanupUploadedFiles(imageUrls, uploadPath);
                     model.addAttribute("categories", categoryService.getActiveCategories());
                     model.addAttribute("productRequestDTO", productRequestDTO); // Keep form data
                     return "Vendor/product/create";
                }


                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                // 3. Server-side Type Probe (More reliable)
                String probedContentType = Files.probeContentType(filePath);
                 if (probedContentType == null || !probedContentType.startsWith("image/")) {
                     // It's not an image file despite the extension/MIME type from the browser
                     Files.deleteIfExists(filePath); // Delete the potentially malicious file
                     model.addAttribute("errorMessage", "Nội dung tệp '" + file.getOriginalFilename() + "' không phải là ảnh.");
                     cleanupUploadedFiles(imageUrls, uploadPath);
                     model.addAttribute("categories", categoryService.getActiveCategories());
                     model.addAttribute("productRequestDTO", productRequestDTO); // Keep form data
                     return "Vendor/product/create";
                 }
                // --- End Safe File Naming and Saving ---

                imageUrls.add(storedFileName); // Add the safely stored filename to the list

            } // End loop over files

        } catch (IOException e) {
            System.err.println("Lỗi khi xử lý tệp tải lên: " + e.getMessage());
            model.addAttribute("errorMessage", "Lỗi khi tải ảnh lên: " + e.getMessage());
             model.addAttribute("categories", categoryService.getActiveCategories());
             model.addAttribute("productRequestDTO", productRequestDTO); // Keep form data
            return "Vendor/product/create";
        }

        // Gọi service để tạo sản phẩm mới
        productRequestDTO.setImageUrls(imageUrls); // Cập nhật danh sách ảnh vào ProductRequestDTO
        try {
            productService.createProduct(productRequestDTO);
       } catch (Exception e) {
            System.err.println("Lỗi khi tạo sản phẩm: " + e.getMessage());
             model.addAttribute("errorMessage", "Lỗi khi tạo sản phẩm: " + e.getMessage());
            // Clean up uploaded files if product creation fails
            cleanupUploadedFiles(imageUrls, uploadPath);
             model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("productRequestDTO", productRequestDTO); // Keep form data
            return "Vendor/product/create";
       }


       return "redirect:/Vendor/products?page=0&size=10"; // Redirect to product list
   }

   // Helper method to clean up files uploaded in case of error
   private void cleanupUploadedFiles(List<String> imageUrls, Path uploadPath) {
       for (String filename : imageUrls) {
           Path filePath = uploadPath.resolve(filename);
           try {
               Files.deleteIfExists(filePath);
               System.out.println("Cleaned up file: " + filename);
           } catch (IOException e) {
               System.err.println("Failed to clean up file: " + filename + " - " + e.getMessage());
           }
       }
   }

   @GetMapping("/uploads/{imageName:.+}") // Use :.+ to allow dot in filename
   @ResponseBody
   public ResponseEntity<Resource> getImage(@PathVariable String imageName) {
       if (imageName == null || imageName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
       }
       try {
           Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
           Path requestedFilePath = uploadPath.resolve(imageName).normalize(); // Resolve safely

           // Important: Check for path traversal! Ensure the requested path is within the upload directory.
           if (!requestedFilePath.startsWith(uploadPath)) {
                System.err.println("Attempted path traversal in getImage (Vendor): " + imageName);
                return ResponseEntity.status(403).body(null); // Forbidden
           }

           File file = requestedFilePath.toFile();

           if (file.exists() && file.isFile()) {
               Resource resource = new FileSystemResource(file);
               // Probe content type for security
               String contentType = Files.probeContentType(file.toPath());
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Default to safe type
                }
                // Optionally, check if probed type is actually an image before serving directly
                if (!contentType.startsWith("image/")) {
                     System.err.println("Attempted to serve non-image file as image (Vendor): " + imageName);
                    // You might return 403 or serve as octet-stream depending on policy
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Serve as binary to prevent script execution
                }


               return ResponseEntity.ok()
                       .contentType(MediaType.parseMediaType(contentType))
                       .body(resource);
           } else {
               return ResponseEntity.notFound().build();
           }
       } catch (IOException e) {
            System.err.println("Lỗi khi truy xuất ảnh (Vendor): " + imageName + " - " + e.getMessage());
            return ResponseEntity.status(500).body(null); // Internal Server Error
       } catch (Exception e) {
            System.err.println("Lỗi không xác định khi truy xuất ảnh (Vendor): " + imageName + " - " + e.getMessage());
            return ResponseEntity.status(500).body(null); // Internal Server Error
       }
   }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Product> productOptional = productService.findById(id);

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            ProductRequestDTO productRequestDTO = new ProductRequestDTO();
            productRequestDTO.setProductId(product.getProductId());
            productRequestDTO.setProductName(product.getProductName());
            productRequestDTO.setPrice(product.getPrice());
            productRequestDTO.setDescription(product.getDescription());
            productRequestDTO.setBrand(product.getBrand());
            productRequestDTO.setExpirationDate(product.getExpirationDate());
            productRequestDTO.setManufactureDate(product.getManufactureDate());
            productRequestDTO.setIngredient(product.getIngredient());
            productRequestDTO.setInstruction(product.getInstruction());
            productRequestDTO.setVolumeOrWeight(product.getVolumeOrWeight());
            productRequestDTO.setBrandOrigin(product.getBrandOrigin());
            productRequestDTO.setStock(product.getStock());
            productRequestDTO.setWarehouse_date_first(product.getWarehouseDateFirst());
            productRequestDTO.setCategoryId(product.getCategory().getCategoryId()); // Lấy ID thể loại
            // Chuyển danh sách ảnh sang URL
           


            model.addAttribute("productRequestDTO", productRequestDTO);
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "Vendor/product/edit"; // Tên file Thymeleaf
        }
        return "redirect:/Vendor/products"; // Nếu không tìm thấy sản phẩm
    }
    
    
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable Integer id, @ModelAttribute ProductRequestDTO productRequestDTO , Model model) {
    	if (productRequestDTO.getManufactureDate().isAfter(productRequestDTO.getExpirationDate())) {
            model.addAttribute("errorMessage", "Ngày sản xuất phải trước ngày hết hạn.");
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "Vendor/product/edit";
        }
        if (productRequestDTO.getWarehouse_date_first().isBefore(productRequestDTO.getManufactureDate()) || 
            productRequestDTO.getWarehouse_date_first().isAfter(productRequestDTO.getExpirationDate())) {
            model.addAttribute("errorMessage", "Ngày nhập kho phải nằm giữa ngày sản xuất và ngày hết hạn.");
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "Vendor/product/edit";
        }
        
        try {
            productService.updateProduct(id, productRequestDTO);
            return "redirect:/Vendor/products";  // Chuyển hướng đến trang danh sách sản phẩm sau khi cập nhật thành công
        } catch (Exception e) {
            e.printStackTrace();
            return "Vendor/product/edit";  // Quay lại trang chỉnh sửa nếu có lỗi
        }
    }
    
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Integer id) {
        productService.setStockToZero(id); 
        return "redirect:/Vendor/products"; 
    }

}

