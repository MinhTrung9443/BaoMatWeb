package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Account;
import vn.iotstar.entity.Vendor;

public interface IVendorService {

	void deleteById(Integer id);

	long count();

	Optional<Vendor> findById(Integer id);

	List<Vendor> findAll();

	Page<Vendor> findAll(Pageable pageable);

	<S extends Vendor> S save(S entity);

	Vendor findByPhone(String phone);

	Vendor findByEmail(String email);

	void deleteByAccountId(Integer accountid);

	Account findByUsername(String username);
	
	List <Vendor> findByFullnameContaining(String fullname);
}
