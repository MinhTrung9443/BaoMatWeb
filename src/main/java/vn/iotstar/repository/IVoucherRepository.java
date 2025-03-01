package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import io.lettuce.core.dynamic.annotation.Param;
import vn.iotstar.entity.Voucher;

@Repository
public interface IVoucherRepository  extends JpaRepository<Voucher, Integer>{
	List<Voucher> findByVoucherCode(String voucherCode);
	
	boolean existsByVoucherCode(String voucherCode);
	@Query("SELECT v FROM Voucher v WHERE v.voucherCode = :voucherCode AND v.active = 1 "
			+ "AND CURRENT_DATE BETWEEN v.startDate AND v.endDate")
	List<Voucher> findValidVoucher(@Param("voucherCode") String voucherCode);
	
	@Query("SELECT v FROM Voucher v WHERE v.active = 1 AND CURRENT_DATE BETWEEN v.startDate AND v.endDate")
	List<Voucher> findValidActiveVouchers();
}
