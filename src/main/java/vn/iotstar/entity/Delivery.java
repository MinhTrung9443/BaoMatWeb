package vn.iotstar.entity;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Delivery")
public class Delivery implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int deliveryId;
	@Column(columnDefinition = "NVARCHAR(MAX)")
	private String deliveryName;
	private int price;
	
	@OneToMany(mappedBy = "delivery",cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Shipper> shipper;
}
