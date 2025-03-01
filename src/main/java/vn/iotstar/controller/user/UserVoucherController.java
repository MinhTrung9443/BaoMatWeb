package vn.iotstar.controller.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.iotstar.entity.Voucher;
import vn.iotstar.repository.IVoucherRepository;

@RestController
@RequestMapping("/api/voucher/check")
public class UserVoucherController {
	@Autowired
	private IVoucherRepository voucherService;
	
	@PostMapping("")
	public ResponseEntity<?> checkVoucher(@RequestParam String voucherCode,@RequestParam long totalPrice) {

        List<Voucher> listVoucher = voucherService.findValidVoucher(voucherCode);

        if (listVoucher.size() == 0) {
            return new ResponseEntity<>("Mã không tồn tại",HttpStatus.BAD_REQUEST);
        }
        
        Voucher voucher = listVoucher.get(0);
        
        if (voucher.getMinimumCost() > totalPrice)
        {
        	return new ResponseEntity<>("Mã không đủ điều kiện áp dụng",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(voucher, HttpStatus.OK);
    }
}
