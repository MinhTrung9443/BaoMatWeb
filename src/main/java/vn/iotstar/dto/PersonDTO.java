package vn.iotstar.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
	private String username;
	private String password;
	private String email;
	private String fullName;
	private int gender;
	private String phone;
	private LocalDate birthday;
	private String role;
}
