# Trang Web Bán Mỹ Phẩm - OneShop

## 1. Giới thiệu
**OneShop** là trang web thương mại điện tử hiện đại, được phát triển với **Java 21**, **Spring Boot 3**, và **Thymeleaf**. Dự án mang đến trải nghiệm duyệt, đánh giá và mua sắm mỹ phẩm dễ dàng.  
Hệ thống sử dụng **JPA** và **Spring Security** để đảm bảo xác thực và phân quyền an toàn. Với giao diện trực quan, thân thiện, người dùng dễ dàng tìm kiếm, so sánh sản phẩm và thực hiện giao dịch với các tùy chọn thanh toán linh hoạt.

---

## 2. Điểm nổi bật
- **Mô phỏng hoạt động thực tế**:  
  Hệ thống được thiết kế như một trang web bán hàng chuyên nghiệp, giải quyết chính xác các nghiệp vụ thực tế.  
- **Ứng dụng các nguyên tắc OOP mạnh mẽ**:  
  Các mối quan hệ giữa các thực thể và thuộc tính được xác định chính xác.  
  (Ví dụ: sản phẩm không có thuộc tính số lượng mà được quản lý qua kho).  
- **Tính năng bảo mật tiên tiến**:
  - Sử dụng **Spring Security** để xác thực và phân quyền người dùng.
  - Kiểm tra trạng thái tài khoản (bị khóa hoặc không) bằng lớp tùy chỉnh khi đăng nhập.
  - Tùy chỉnh xử lý đăng nhập thành công và thất bại.
  - Gán vai trò cho người dùng khi đăng nhập bằng **OAuth2**.

---

## 3. Công nghệ sử dụng
- **Backend**: Java 21, Spring Boot 3, Spring Security
- **Frontend**: Thymeleaf, HTML, CSS, JavaScript
- **Cơ sở dữ liệu**: SQL Server
- **ORM**: JPA (Java Persistence API)
- **Quản lý phiên bản**: Git, GitHub
- **Công cụ hỗ trợ**: Spring Boot, SQL Server

---

## 4. Hỗ trợ nhiều loại xác thực
- Đăng nhập mặc định của Spring Security.
- Đăng nhập **Remember-me** sử dụng cookie.

---

## 5. Truy cập vào Web
- Mở trình duyệt web của bạn và truy cập:  
  `http://localhost:8080`

---

## 6. Hướng dẫn cài đặt
### 6.1. Yêu cầu cài đặt
Trước khi triển khai, đảm bảo bạn đã cài đặt các công cụ và môi trường sau:
- **Spring tool suite, Eclipse hoặc IDE**
- **Java Development Kit (JDK) 21**
- **Apache Maven**
- **SQL Server**
- **Apache Tomcat (nếu không sử dụng embedded server của Spring Boot)**

### 6.2. Cài đặt
**Clone repository**:  
   ```bash
   git clone https://github.com/MinhTrung9443/BaoMatWeb.git
   ```
### 6.3. Cấu hình cơ sở dữ liệu
**Tạo database trong SQL Server**.
 Cập nhật application.properties:
 ```bash
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=WebProject
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
 ```
🔥 ⭐



