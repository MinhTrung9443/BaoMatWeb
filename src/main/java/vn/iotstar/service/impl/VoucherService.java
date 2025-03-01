package vn.iotstar.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Voucher;
import vn.iotstar.repository.IVoucherRepository;
import vn.iotstar.service.IVoucherService;

@Service
public class VoucherService implements IVoucherService {

	@Autowired
	private IVoucherRepository voucherRepository;

	@Override
	public <S extends Voucher> S save(S entity) {
		return voucherRepository.save(entity);
	}

	@Override
	public Page<Voucher> findAll(Pageable pageable) {
		return voucherRepository.findAll(pageable);
	}

	@Override
	public List<Voucher> findAll() {
		return voucherRepository.findAll();
	}

	@Override
	public Optional<Voucher> findById(Integer id) {
		return voucherRepository.findById(id);
	}

	@Override
	public List<Voucher> findByVoucherCode(String voucherCode) {
		return voucherRepository.findByVoucherCode(voucherCode);
	}

	@Override
	public long count() {
		return voucherRepository.count();
	}

	@Override
	public void deleteById(Integer id) {
		voucherRepository.deleteById(id);
	}

	@Override
	public void delete(Voucher entity) {
		voucherRepository.delete(entity);
	}

	@Override
	public boolean existsByVoucherCode(String voucherCode) {
		return voucherRepository.existsByVoucherCode(voucherCode);
	}

	@Override
	public Voucher findRandomValidActiveVoucher() {
		List<Voucher> vouchers = voucherRepository.findValidActiveVouchers();
		if (vouchers.isEmpty()) {
            return null; 
        }
        int randomIndex = new Random().nextInt(vouchers.size());
        return vouchers.get(randomIndex);
	}
	
	
	
}
