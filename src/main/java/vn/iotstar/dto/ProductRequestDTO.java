package vn.iotstar.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ProductRequestDTO {

	private int productId;
	 	private String productName;
	    private Integer price;
	    private String description;
	    private String brand;
		private LocalDate expirationDate;
		private LocalDate manufactureDate;
	    private String ingredient;
	    private String instruction;
	    private String volumeOrWeight;
	    private String brandOrigin;
	    private Long stock;
	    private LocalDate warehouse_date_first;
	    private Integer categoryId; // ID của thể loại
	    private List<String> imageUrls; // Danh sách URL ảnh
}
