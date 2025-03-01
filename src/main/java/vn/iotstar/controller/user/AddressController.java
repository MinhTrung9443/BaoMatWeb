package vn.iotstar.controller.user;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.Address;
import vn.iotstar.entity.Person;
import vn.iotstar.service.IAddressService;
import vn.iotstar.service.IUserService;


@Controller
@RequestMapping("/User/Address") 
public class AddressController {
	@Autowired
    private IAddressService addressService;
	@Autowired
    private IUserService userService;
	
	@PostMapping("/edit")
	public ResponseEntity<String> editAdress(ModelMap model, HttpSession session, @RequestParam int addressId, 
			@RequestParam String addressType, @RequestParam String addressDetail)
	{
		Optional<Address> add = addressService.findById(addressId);
		if(add.isPresent())
		{
			Address address = add.get();
			address.setAddressType(addressType);
			address.setAddressDetail(addressDetail);
			addressService.save(address);
			return ResponseEntity.ok("Cập nhật thành công");
		}
		return ResponseEntity.badRequest().body("Không tìm thấy");
	}
	
	@PostMapping("/add")
	public ResponseEntity<String> addAdress(ModelMap model, HttpSession session, @RequestParam String newAddressType, @RequestParam String newAddressDetail)
	{
		
		Person user = (Person) session.getAttribute("user");
		
		Address address = new Address();
		address.setAddressType(newAddressType);
		address.setAddressDetail(newAddressDetail);
		address.setUser(user);

		addressService.save(address);
		return ResponseEntity.ok("Thêm thành công");
	}
	@PostMapping("/delete")
	public ResponseEntity<String> deleteAddress(ModelMap model, HttpSession session, @RequestParam int addressId)
	{
		
		Optional<Address> address = addressService.findById(addressId);
		if (address.isPresent()) {
			Address delAdd = address.get();
			addressService.deleteById(delAdd.getAddressId());
			return ResponseEntity.ok("Xóa thành công");
		}
		return ResponseEntity.badRequest().body("Xóa thành công");
	}
}
