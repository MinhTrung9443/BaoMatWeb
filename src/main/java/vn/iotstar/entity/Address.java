package vn.iotstar.entity;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringExclude;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Address")
public class Address implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int addressId;
	@Column(columnDefinition = "NVARCHAR(MAX)")
	private String addressDetail;
	@Column(columnDefinition = "NVARCHAR(MAX)")
	private String addressType;
	
	@ManyToOne()
	@JoinColumn(name = "id")
	@JsonBackReference
	@ToStringExclude
	private Person user;
}
