package vn.iotstar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Account;
import vn.iotstar.entity.Shipper;
import vn.iotstar.repository.IAccountRepository;
import vn.iotstar.repository.IShipperRepository;
import vn.iotstar.service.IShipperService;

@Service
public class ShipperService implements IShipperService{
	
	@Autowired
	IShipperRepository shipperRepository;
	
	@Autowired
	IAccountRepository accountRepository;
	
	public ShipperService(IShipperRepository shipperRepository) {
		this.shipperRepository = shipperRepository;
	}

	@Override
	public <S extends Shipper> S save(S entity) {
		return shipperRepository.save(entity);
	}

	@Override
	public Page<Shipper> findAll(Pageable pageable) {
		return shipperRepository.findAll(pageable);
	}

	@Override
	public Optional<Shipper> findById(Integer id) {
		return shipperRepository.findById(id);
	}

	@Override
	public long count() {
		return shipperRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		shipperRepository.deleteById(id);
	}

	@Override
	public List<Shipper> findAll() {
		return shipperRepository.findAll();
	}
	
	@Override
	public void deleteByAccountId(Integer accountId) {
		accountRepository.deleteById(accountId);
	}
	
	@Override
	public Account findByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	@Override
	public Shipper findByEmail(String email) {
		return shipperRepository.findByEmail(email);
	}

	@Override
	public Shipper findByPhone(String phone) {
		return shipperRepository.findByPhone(phone);
	}

	@Override
	public List<Shipper> findByFullnameContaining(String fullname) {
		return shipperRepository.findByFullnameContaining(fullname);
	}
	@Override
	public Shipper findByPersonId(int id) {
		return shipperRepository.findById(id).get();
	}

	@Override
	public Shipper findByAccount_AccountId(int accountId) {
		return shipperRepository.findByAccount_AccountId(accountId);
	}
}
