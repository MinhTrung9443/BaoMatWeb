package vn.iotstar.controller.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import vn.iotstar.entity.Account;
import vn.iotstar.entity.Delivery;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.Shipper;
import vn.iotstar.service.IDeliveryService;
import vn.iotstar.service.IShipperService;

@Controller
@RequestMapping("/Admin/shipper")
public class ManageShipperController {

	@Autowired
	IShipperService shipperService;

	@Autowired
	IDeliveryService deliveryService;

	List<String> districts = Arrays.asList("Quận 1", "Quận 2", "Quận 3", "Quận 4", "Quận 5", "Quận 6", "Quận 7",
			"Quận 8", "Quận 9", "Quận 10", "Quận 11", "Quận 12", "Bình Thạnh", "Tân Bình", "Tân Phú", "Phú Nhuận",
			"Gò Vấp", "Bình Tân");

	@RequestMapping("")
	public String getShipper(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {

		int count = (int) shipperService.count();
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);

		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

		int totalPages = shipperService.findAll(pageable).getTotalPages();
		Page<Shipper> list = shipperService.findAll(pageable);
		int start = Math.max(1, currentPage - 2);
		int end = Math.min(currentPage + 2, totalPages);

		if (totalPages > count) {
			if (end == totalPages)
				start = end - count;
			else if (start == 1) {
				end = start + count;
			}
		}

		List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

		model.addAttribute("list", list);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("pageNumbers", pageNumbers);
		return "Admin/shipper/list";
	}
	
	 @GetMapping("/searchShipper")
	   	public String search(Model model, @RequestParam(value = "fullname", required = false) String fullname) {
	       	
	       	List<Shipper> list = new ArrayList<>();
	   		if (StringUtils.hasText(fullname)) {
	   			List<Shipper> shipper = shipperService.findByFullnameContaining(fullname);
	   			if (!shipper.isEmpty()) {
	   				list = shipper;			
	   			}
	   			else {
	   				model.addAttribute("message", "KHÔNG CÓ KẾT QUẢ NÀO ĐƯỢC TÌM THẤY");
	   			}		
	   		}
	   		model.addAttribute("list",list);
	   		return "Admin/shipper/search";
	   	}

	@GetMapping("/detail/{id}")
	public String viewUserDetail(@PathVariable("id") int id, Model model) {
		Optional<Shipper> optShipper = shipperService.findById(id);
		if (optShipper.isPresent()) {
			Shipper shipper = optShipper.get();
			model.addAttribute("shipper", shipper);
			return "Admin/shipper/detail";
		}
		return "Admin/shipper";
	}

	@GetMapping("/add")
	public String addShipper(Model model) {
		Shipper shipper = new Shipper();
		Account account = new Account();
		Delivery delivery = new Delivery();
		List<Delivery> deliveries = deliveryService.findAll();

		shipper.setAccount(account);
		shipper.setDelivery(delivery);

		model.addAttribute("shipper", shipper);
		model.addAttribute("deliveries", deliveries);
		model.addAttribute("districts", districts);
		return "Admin/shipper/add";
	}

	@GetMapping("/edit/{id}")
	public ModelAndView editShipper(ModelMap model, @PathVariable("id") Integer id) {
		Optional<Shipper> optShipper = shipperService.findById(id);
		List<Delivery> deliveries = deliveryService.findAll();

		Shipper shipper = new Shipper();
		if (optShipper.isPresent()) {
			Shipper entity = optShipper.get();

			BeanUtils.copyProperties(entity, shipper);

			model.addAttribute("shipper", shipper);
			model.addAttribute("deliveries", deliveries);
			model.addAttribute("districts", districts);

			return new ModelAndView("Admin/shipper/edit", model);
		}
		return new ModelAndView("forward:/Admin/shipper", model);
	}

	// kiểm tra sdt tồn tại
	public boolean checkExistPhone(ModelMap model, Shipper shipper, int id) {
		if (shipperService.findByPhone(shipper.getPhone()) != null) {
			if (id != 0) {
				if (shipperService.findByPhone(shipper.getPhone()).getId() != id) {
					model.addAttribute("checkPhone", "SỐ ĐIỆN THOẠI NÀY ĐÃ TỒN TẠI!");
					return true;
				}
			} else {
				model.addAttribute("checkPhone", "SỐ ĐIỆN THOẠI NÀY ĐÃ TỒN TẠI!");
				return true;
			}
		}
		return false;
	}

	public boolean checkExistEmail(ModelMap model, Shipper shipper, int id) {
		if (shipperService.findByEmail(shipper.getEmail()) != null) {
			if (id != 0) {
				if (shipperService.findByEmail(shipper.getEmail()).getId() != id) {
					model.addAttribute("existEmail", "EMAIL NÀY ĐÃ TỒN TẠI!");
					return true;
				}
			} else {
				model.addAttribute("existEmail", "EMAIL NÀY ĐÃ TỒN TẠI!");
				return true;
			}
		}
		return false;
	}

	public boolean checkExistUsername(ModelMap model, Account account, int id) {
		if (shipperService.findByUsername(account.getUsername()) != null) {
			model.addAttribute("existUsername", "USERNAME NÀY ĐÃ TỒN TẠI!");
			return true;
		}
		return false;
	}

	public boolean checkPhoneValid(ModelMap model, Shipper shipper) {
		String phoneRegex = "^\\d{10}$";
		if (!Pattern.matches(phoneRegex, shipper.getPhone())) {
			model.addAttribute("checkPhone", "SỐ ĐIỆN THOẠI PHẢI CÓ 10 SỐ!");
			return true;
		}
		return false;
	}
	@Autowired
	private PasswordEncoder passwordEncoder;
	@PostMapping("/create")
	public ModelAndView createShipper(ModelMap model, @Valid @ModelAttribute Shipper shipper, BindingResult result,
			@Valid @ModelAttribute Account account, @RequestParam Integer deliveryId) {
		if (result.hasErrors()) {
			model.addAttribute("shipper", shipper);
			return new ModelAndView("Admin/shipper/add", model);
		}
		int id = shipper.getId();

		boolean check = false;
		if (checkExistPhone(model, shipper, id) || checkExistEmail(model, shipper, id)
				|| checkExistUsername(model, account, id) || checkPhoneValid(model, shipper)) {
			check = true;
		}
		if (check) {
			Shipper ship = new Shipper();
			BeanUtils.copyProperties(shipper, ship);
			ship.setAccount(account);

			List<Delivery> deliveries = deliveryService.findAll();

			model.addAttribute("deliveries", deliveries);
			model.addAttribute("districts", districts);
			model.addAttribute("shipper", ship);

			return new ModelAndView("Admin/shipper/add", model);
		}

		Optional<Delivery> optDelivery = deliveryService.findById(deliveryId);

		Delivery delivery = optDelivery.get();
		shipper.setDelivery(delivery);

		Shipper entity = new Shipper();
		Account tk = new Account();
		BeanUtils.copyProperties(shipper, entity);
		BeanUtils.copyProperties(account, tk);
		tk.setPassword(passwordEncoder.encode(account.getPassword()));
		entity.setAccount(tk);
		tk.setRole(new Role(4, "shipper"));
		entity.setAccount(tk);

		shipperService.save(entity);

		return new ModelAndView("forward:/Admin/shipper", model);
	}

	@PostMapping("/edit")
	public ModelAndView editShipper(ModelMap model, @Valid @ModelAttribute Shipper shipper, BindingResult result,
			@Valid @ModelAttribute Account account, @RequestParam Integer deliveryId) {
		if (result.hasErrors()) {
			model.addAttribute("shipper", shipper);

			return new ModelAndView("Admin/shipper/edit", model);

		}
		int id = shipper.getId();
		Shipper oldShipper = shipperService.findById(id).get();

		boolean check = false;
		if (checkExistPhone(model, shipper, id) || checkExistEmail(model, shipper, id)
				|| checkPhoneValid(model, shipper)) {
			check = true;
		}
		if (check) {
			Shipper ship = new Shipper();
			BeanUtils.copyProperties(shipper, ship);

			List<Delivery> deliveries = deliveryService.findAll();
			model.addAttribute("deliveries", deliveries);
			model.addAttribute("districts", districts);

			model.addAttribute("shipper", ship);

			return new ModelAndView("Admin/shipper/edit", model);
		}

		Optional<Delivery> optDelivery = deliveryService.findById(deliveryId);

		Delivery delivery = optDelivery.get();
		shipper.setDelivery(delivery);

		Account oldAccount = oldShipper.getAccount();
		oldAccount.setActive(account.getActive());
		BeanUtils.copyProperties(shipper, oldShipper);
		oldShipper.setAccount(oldAccount);

		shipperService.save(oldShipper);

		return new ModelAndView("forward:/Admin/shipper", model);
	}

	@Transactional
	@GetMapping("/delete/{id}")
	public ModelAndView delete(ModelMap model, @PathVariable("id") int id) {
		Optional<Shipper> optShipper = shipperService.findById(id);
		String message = "";
		if (optShipper.isPresent()) {

			Account acc = optShipper.get().getAccount();
			Integer accountid = acc.getAccountId();

			shipperService.deleteByAccountId(accountid);

			shipperService.deleteById(id);
			
			message = "Xoá thành công";
			model.addAttribute("message", message);

			List<Shipper> list = shipperService.findAll();
			model.addAttribute("list", list);
			return new ModelAndView("Admin/shipper/list", model);
		}
		return new ModelAndView("forward:/Admin/shipper", model);
	}
}
