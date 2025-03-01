package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Account;
import vn.iotstar.entity.Person;
import vn.iotstar.service.IAccountService;
import vn.iotstar.service.IUserService;


@Service
public class UserServiceImpl implements UserDetailsService{
	@Autowired
	private IAccountService repo;
	@Autowired
	private IUserService userService;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = repo.findByUsername(username);
		Person user = userService.findByAccountUsername(username);
		if (account == null)
		{
			throw new UsernameNotFoundException("Could not find user");
		}
		
		return new MyUserService(user);
	}
	
}
