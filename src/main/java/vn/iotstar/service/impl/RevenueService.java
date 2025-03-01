package vn.iotstar.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import vn.iotstar.dto.RevenueByMonthDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.OrderLine;
import vn.iotstar.entity.Product;
import vn.iotstar.enums.OrderStatus;
import vn.iotstar.repository.IOrderLineRepository;
import vn.iotstar.repository.IOrderRepository;
import vn.iotstar.repository.IProductRepository;
import vn.iotstar.service.IRevenueService;
import java.text.DecimalFormat;

@Service
public class RevenueService implements IRevenueService{
	 @Autowired
	    private IOrderRepository orderRepository;

	    @Autowired
	    private IOrderLineRepository orderLineRepository;

	    @Autowired
	    private IProductRepository productRepository;

	    @Autowired
	    private EntityManager entityManager;
	    
	 // Trong phương thức Service
	    DecimalFormat df = new DecimalFormat("#,###.00");

	    public RevenueStats getRevenueStats() {
	        // Tổng số sản phẩm bán được
	        List<Order> completedOrders = orderRepository.findByOrderStatus(OrderStatus.COMPLETED);
	        long totalProductsSold = 0;
	        double totalRevenue = 0.0;

	        for (Order order : completedOrders) {
	            List<OrderLine> orderLines = order.getLines();
	            for (OrderLine orderLine : orderLines) {
	                totalProductsSold += orderLine.getQuantity();
	                Product product = orderLine.getProduct();
	                totalRevenue += product.getPrice() * orderLine.getQuantity();
	            }
	        }

	        // Định dạng doanh thu
	        String formattedRevenue = df.format(totalRevenue);

	        // Trả về thống kê doanh thu với doanh thu đã được định dạng
	        return new RevenueStats(totalProductsSold, formattedRevenue);  // Trả về doanh thu đã định dạng
	    }
	 // Phương thức tính toán thống kê doanh thu theo thể loại sản phẩm
	    public List<CategoryRevenueStats> getCategoryRevenueStats() {
	        // Lấy danh sách các đơn hàng đã hoàn thành
	        List<Order> completedOrders = orderRepository.findByOrderStatus(OrderStatus.COMPLETED);
	        
	        // Danh sách lưu trữ kết quả thống kê theo thể loại sản phẩm
	        List<CategoryRevenueStats> categoryStatsList = new ArrayList<>();
	        
	        // Tính toán doanh thu theo từng thể loại sản phẩm
	        for (Order order : completedOrders) {
	            for (OrderLine orderLine : order.getLines()) {
	                Product product = orderLine.getProduct();
	                int quantity = orderLine.getQuantity();
	                double totalPrice = product.getPrice() * quantity;

	                // Kiểm tra xem thể loại sản phẩm đã có trong danh sách thống kê chưa
	                Category category = product.getCategory();
	                CategoryRevenueStats categoryStats = findCategoryStats(categoryStatsList, category);

	                if (categoryStats == null) {
	                    // Nếu thể loại chưa có, thêm mới
	                    categoryStats = new CategoryRevenueStats(category.getCategoryName(), 0, 0.0);
	                    categoryStatsList.add(categoryStats);
	                }

	                // Cập nhật số lượng và doanh thu cho thể loại
	                categoryStats.setTotalQuantity(categoryStats.getTotalQuantity() + quantity);
	                categoryStats.setTotalRevenue(categoryStats.getTotalRevenue() + totalPrice);
	            }
	        }

	        // Định dạng doanh thu
	        for (CategoryRevenueStats stats : categoryStatsList) {
	            String formattedRevenue = df.format(stats.getTotalRevenue());
	            stats.setFormattedRevenue(formattedRevenue);
	        }

	        return categoryStatsList;
	    }

	    // Hàm tìm thống kê thể loại đã có trong danh sách
	    private CategoryRevenueStats findCategoryStats(List<CategoryRevenueStats> categoryStatsList, Category category) {
	        for (CategoryRevenueStats stats : categoryStatsList) {
	            if (stats.getCategoryName().equals(category.getCategoryName())) {
	                return stats;
	            }
	        }
	        return null;
	    }
	    

	    public List<RevenueByMonthDTO> getRevenueByMonth() {
	        List<Object[]> results = orderRepository.getRevenueByMonth();
	        List<RevenueByMonthDTO> revenueList = new ArrayList<>();
	        System.out.println(revenueList);

	        for (Object[] result : results) {
	            String month = (String) result[0];
	            int revenue = ((Number) result[1]).intValue(); // Đảm bảo đúng kiểu dữ liệu
	            revenueList.add(new RevenueByMonthDTO(month, revenue));
	        }

	        return revenueList;
	    }

}
