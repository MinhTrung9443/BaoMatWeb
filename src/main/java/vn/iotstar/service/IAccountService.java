package vn.iotstar.service;

import org.springframework.stereotype.Service;

import vn.iotstar.entity.Account;

@Service
public interface IAccountService {

	// Method to log in by username and password
	Account login(String username, String password);


	Account findById(int i);

	boolean resetPassword(String token, String newPassword); // Thêm phương thức reset password

	// Method to save the account (after resetting password or other updates)
	void save(Account account);

	void saveRememberMe(String username, String password);

	void deleteRememberMe(String username);

	String generateResetToken(String email);

	Account findByToken(String resetToken);

	void update(Account account);

	void deleteById(Integer id);

	Account findByUsername(String username);

}
