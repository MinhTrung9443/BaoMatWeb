package vn.iotstar.service.impl;



import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Address;
import vn.iotstar.repository.IAddressRepository;
import vn.iotstar.service.IAddressService;

@Service
public class AddressService implements IAddressService {
	@Autowired
	private IAddressRepository addressRepository;

	@Override
	public <S extends Address> S save(S entity) {
		return addressRepository.save(entity);
	}

	@Override
	public Optional<Address> findById(Integer id) {
		return addressRepository.findById(id);
	}

	@Override
	public void deleteById(Integer id) {
		addressRepository.deleteById(id);
	}

	
}
