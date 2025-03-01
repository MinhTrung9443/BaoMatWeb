package vn.iotstar.entity;

import java.io.Serializable;

import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "OrderAssignment")
public class OrderAssignment implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private int orderAssignmentId;
	
	@OneToOne
	@JoinColumn(name = "orderId")
	private Order order;
	
	@ManyToOne()
	@JoinColumn(name = "id")
	private Shipper shipper;
	
}
