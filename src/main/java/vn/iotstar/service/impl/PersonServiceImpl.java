package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Person;
import vn.iotstar.entity.User;
import vn.iotstar.repository.IPersonRepository;
import vn.iotstar.service.IPersonService;
@Service
public class PersonServiceImpl implements IPersonService{
	 @Autowired
	    private IPersonRepository personRepository;
	  @Override
	    public Person findByEmail(String email) {
	        return personRepository.findByEmail(email);
	    }
	@Override
	public void save(Person user) {
		personRepository.save(user);
		
	}
	@Override
	public Person findByPhone(String phone) {
		return personRepository.findByPhone(phone);
	}
	  
}
