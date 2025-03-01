package vn.iotstar.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import vn.iotstar.dto.PersonDTO;

import vn.iotstar.entity.Account;
import vn.iotstar.entity.Admin;

import vn.iotstar.entity.Role;
import vn.iotstar.service.IRoleService;
import vn.iotstar.service.IUserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private IRoleService roleService;
	@Autowired IUserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody PersonDTO user)
	{
		System.out.println(user.toString());
		Role role = roleService.findByName(user.getRole());
		Account accout = new Account();
		BeanUtils.copyProperties(user, accout);
		accout.setRole(role);
		accout.setPassword(passwordEncoder.encode(user.getPassword()));
		Admin admin = new Admin();
		
		BeanUtils.copyProperties(user, admin);
		admin.setAccount(accout);
		
		userService.save(admin);
		
		return new ResponseEntity<>("Đăng ký thành công!",HttpStatus.OK);
	}
}
