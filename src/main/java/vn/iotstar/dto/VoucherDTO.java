package vn.iotstar.dto;

import java.time.LocalDateTime;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
	
	private int voucherId;
	private String voucherType;
	private String voucherCode;
	private int voucherValue;	
	private int minimumCost;
	private LocalDateTime startDate;	
	private LocalDateTime endDate;	
	private int active;
	private int quantity;
}
