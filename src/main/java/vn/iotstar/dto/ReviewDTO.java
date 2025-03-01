package vn.iotstar.dto;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	private int feedbackId;
	@Column(columnDefinition = "nvarchar(max)")
	private String comment;
	private LocalDateTime reviewDate;
	private int rating;
	private MultipartFile image;
}
