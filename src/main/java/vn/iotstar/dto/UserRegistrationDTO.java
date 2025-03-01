package vn.iotstar.dto;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
	private String username;
	private String password;
	private String email;
	private String fullname;
	private String gender;
	private String phone;
	private LocalDate birthday;
	private String address_detail;
	private String address_type;
}
