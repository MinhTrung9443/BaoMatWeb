package vn.iotstar.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.iotstar.entity.Address;

@Service
public interface IAddressService {

	void deleteById(Integer id);

	Optional<Address> findById(Integer id);

	<S extends Address> S save(S entity);

}
