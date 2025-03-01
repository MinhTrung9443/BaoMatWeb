package vn.iotstar.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Vendor")
public class Vendor extends Person implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int salary;
	private LocalDateTime startDate;
	@Column(columnDefinition = "nvarchar(max)")
	
	
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Address> address;
	

	@OneToMany(mappedBy= "user", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<Favourite> favourite;
	
	@OneToMany(mappedBy= "user", cascade = CascadeType.ALL)
	@JsonManagedReference
	private List<ViewHistory> viewHistory;
}
