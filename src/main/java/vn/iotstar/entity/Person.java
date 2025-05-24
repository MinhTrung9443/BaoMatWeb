package vn.iotstar.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Person")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;
	@Column(columnDefinition = "NVARCHAR(MAX)")
	protected String fullname;
	protected String phone;
	protected int gender;
	protected String email;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "accountId")
	@JsonManagedReference
	protected Account account;


}
