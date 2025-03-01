package vn.iotstar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Account;
import vn.iotstar.entity.Vendor;
import vn.iotstar.repository.IAccountRepository;
import vn.iotstar.repository.IVendorRepository;
import vn.iotstar.service.IVendorService;

@Service
public class VendorService implements IVendorService {
	
	@Autowired
	IVendorRepository venRepository;
	
	@Autowired
	IAccountRepository accountRepository;

	public VendorService(IVendorRepository venRepository) {
		this.venRepository = venRepository;
	}

	@Override
	public Vendor findByEmail(String email) {
		return venRepository.findByEmail(email);
	}

	@Override
	public Vendor findByPhone(String phone) {
		return venRepository.findByPhone(phone);
	}

	@Override
	public <S extends Vendor> S save(S entity) {
		return venRepository.save(entity);
	}

	@Override
	public Page<Vendor> findAll(Pageable pageable) {
		return venRepository.findAll(pageable);
	}

	@Override
	public List<Vendor> findAll() {
		return venRepository.findAll();
	}

	@Override
	public Optional<Vendor> findById(Integer id) {
		return venRepository.findById(id);
	}

	@Override
	public long count() {
		return venRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		venRepository.deleteById(id);
	}

	@Override
	public void deleteByAccountId(Integer accountid) {
		accountRepository.deleteById(accountid);		
	}
	
	@Override
	public Account findByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	@Override
	public List<Vendor> findByFullnameContaining(String fullname) {
		return venRepository.findByFullnameContaining(fullname);
	}
	
	

}
