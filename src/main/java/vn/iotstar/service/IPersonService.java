package vn.iotstar.service;

import org.springframework.stereotype.Service;

import vn.iotstar.entity.Person;
import vn.iotstar.entity.User;
@Service
public interface IPersonService {
	Person findByPhone(String phone);

	Person findByEmail(String email);

	void save(Person user);
}
