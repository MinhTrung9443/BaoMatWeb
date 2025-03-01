package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Voucher;

public interface IVoucherService {

	void delete(Voucher entity);

	void deleteById(Integer id);

	long count();

	Optional<Voucher> findById(Integer id);

	List<Voucher> findAll();

	Page<Voucher> findAll(Pageable pageable);

	<S extends Voucher> S save(S entity);

	List<Voucher> findByVoucherCode(String voucherCode);
	
	boolean existsByVoucherCode(String voucherCode);

	Voucher findRandomValidActiveVoucher();

}
