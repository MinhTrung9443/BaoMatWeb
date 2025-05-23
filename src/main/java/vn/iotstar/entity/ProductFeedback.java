package vn.iotstar.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "ProductFeedback")
public class ProductFeedback implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private int feedbackId;
	@Column(columnDefinition = "text")
	private String comment;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
	private LocalDateTime reviewDate;
	private int rating;
	private String image;
	
	@ManyToOne()
	@JoinColumn(name = "productId")
	@JsonManagedReference
	private Product product;
	
	@ManyToOne()
	@JoinColumn(name = "id")
	private Person user;
}
