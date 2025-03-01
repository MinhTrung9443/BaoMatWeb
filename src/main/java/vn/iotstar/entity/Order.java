package vn.iotstar.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.iotstar.enums.OrderStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "[Order]")
public class Order implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private int orderId;
	private LocalDateTime orderDate;
	@Column(columnDefinition = "nvarchar(max)")
	private String shippingAddress;
    @Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	
    @Column(name = "completion_time")
    private LocalDateTime completionTime;

	@OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<OrderLine> lines;
	
	@OneToOne(mappedBy = "order",cascade = CascadeType.ALL)
	@JsonManagedReference
	private Voucher vouchers;

	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "paymentId")
	@JsonManagedReference
	private Payment payment;
	
	@ManyToOne
	@JoinColumn(name = "deliveryId")
	private Delivery delivery;
	
	@ManyToOne
	@JoinColumn(name="id")
	private Person user;
}
