package vn.iotstar.entity;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Shipper")
public class Shipper extends Person implements Serializable {

	@Column(columnDefinition = "NVARCHAR(MAX)")
	private String address;
	
	@Column(columnDefinition = "NVARCHAR(MAX)")
	private String deliveryArea;
	
	@ManyToOne()
	@JoinColumn(name = "deliveryId")
	@JsonBackReference 
	private Delivery delivery;
	

	@OneToMany(mappedBy = "shipper",cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<OrderAssignment> orderAss;
}
