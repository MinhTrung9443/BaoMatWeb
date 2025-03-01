package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Account;
import vn.iotstar.entity.Shipper;

public interface IShipperService {

	void deleteById(Integer id);

	long count();

	Optional<Shipper> findById(Integer id);

	Page<Shipper> findAll(Pageable pageable);

	<S extends Shipper> S save(S entity);

	List<Shipper> findAll();

	void deleteByAccountId(Integer accountId);

	Account findByUsername(String username);

	Shipper findByPhone(String phone);

	Shipper findByEmail(String email);
	
	List<Shipper> findByFullnameContaining(String fullname);
	Shipper findByPersonId(int id);

	Shipper findByAccount_AccountId(int accountId);
}
