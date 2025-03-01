package vn.iotstar.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Role;
import vn.iotstar.entity.*;
import vn.iotstar.repository.IUserRepository;
import vn.iotstar.service.IAccountService;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
	@Autowired
	private IUserRepository repo;
	@Autowired
	private IAccountService accountService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account user = accountService.findByUsername(username);
		 if (user == null) {
		        throw new UsernameNotFoundException("User not found with username or email: " + username);
		    }
		 System.out.println("Trong CustomerUserDetail "+ user.toString());
		return new org.springframework.security.core.userdetails.User(user.getUsername(),
				user.getPassword(), mapRoleToAuthority(user.getRole()));
		
	}

	private Collection<? extends GrantedAuthority> mapRoleToAuthority(Role role) {
		return Collections.singletonList(new SimpleGrantedAuthority(role.getRoleName())); 
	}

}
