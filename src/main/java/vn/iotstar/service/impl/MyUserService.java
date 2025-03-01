package vn.iotstar.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import vn.iotstar.entity.Role;
import vn.iotstar.entity.*;

public class MyUserService implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Person user;

	public MyUserService(Person user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Role roles = user.getAccount().getRole();
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(roles.getRoleName()));
		System.out.println("Trong myUserService" + authorities);
		
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getAccount().getPassword();
	}

	@Override
	public String getUsername() {
		return user.getAccount().getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return user.getAccount().getActive() == 1 ? true : false;
	}
}
