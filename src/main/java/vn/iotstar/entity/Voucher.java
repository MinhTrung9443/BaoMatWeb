package vn.iotstar.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Voucher")
public class Voucher implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private int voucherId;
	
	@Column( columnDefinition = "NVARCHAR(255)")
	private String voucherType;
	
	private String voucherCode;
	private int voucherValue;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private int active;
	
	@Column(name = "minimum_cost",  columnDefinition = "int" )
	private Integer minimumCost;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "orderId")
	private Order order;
}
