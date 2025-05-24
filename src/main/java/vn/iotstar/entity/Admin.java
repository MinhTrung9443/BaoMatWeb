package vn.iotstar.entity;

import java.io.Serializable;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Admin")
public class Admin extends Person implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(columnDefinition = "NVARCHAR(MAX)")
	private String address;
}
