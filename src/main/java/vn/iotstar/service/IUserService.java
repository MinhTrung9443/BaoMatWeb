package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.User;


import org.springframework.stereotype.Service;

import vn.iotstar.entity.Person;


@Service
public interface IUserService {

	void delete(User entity);

	void deleteById(Integer id);

	long count();
	

	Page<User> findAll(Pageable pageable);

	<S extends User> S save(S entity);

	List<User> findAll();

	List<User> findByFullnameContaining(String fullname);

	Person findByAccountUsername(String username);

	<S extends Person> S save(S entity);

	Optional<Person> findById(Integer id);

}
