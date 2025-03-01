package vn.iotstar.controller.admin;

import java.util.ArrayList;
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
import vn.iotstar.entity.Role;
import vn.iotstar.entity.Vendor;
import vn.iotstar.service.IVendorService;

@Controller
@RequestMapping("/Admin/vendor")
public class ManageVendorController {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	IVendorService vendorService;

	@RequestMapping("")
	public String getVendor(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {

		int count = (int) vendorService.count();
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);

		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

		int totalPages = vendorService.findAll(pageable).getTotalPages();
		Page<Vendor> list = vendorService.findAll(pageable);
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
		return "Admin/vendor/list";
	}

	@GetMapping("/searchVendor")
	public String search(Model model, @RequestParam(value = "fullname", required = false) String fullname) {

		List<Vendor> list = new ArrayList<>();
		if (StringUtils.hasText(fullname)) {
			List<Vendor> vendor = vendorService.findByFullnameContaining(fullname);
			if (!vendor.isEmpty()) {
				list = vendor;
			} else {
				model.addAttribute("message", "KHÔNG CÓ KẾT QUẢ NÀO ĐƯỢC TÌM THẤY");
			}
		}
		model.addAttribute("list", list);
		return "Admin/vendor/search";
	}

	@GetMapping("/detail/{id}")
	public String viewUserDetail(@PathVariable("id") int id, Model model) {
		Optional<Vendor> optVendor = vendorService.findById(id);
		if (optVendor.isPresent()) {
			Vendor vendor = optVendor.get();
			model.addAttribute("vendor", vendor);
			return "Admin/vendor/detail";
		}
		return "Admin/vendor/detail";
	}

	@GetMapping("/add")
	public String addvendor(Model model) {
		Vendor vendor = new Vendor();
		Account account = new Account();

		vendor.setAccount(account);
		model.addAttribute("vendor", vendor);
		return "Admin/vendor/add";
	}

	@GetMapping("/edit/{id}")
	public ModelAndView editVendor(ModelMap model, @PathVariable("id") Integer id) {
		Optional<Vendor> optVendor = vendorService.findById(id);

		Vendor vendor = new Vendor();
		if (optVendor.isPresent()) {
			Vendor entity = optVendor.get();

			BeanUtils.copyProperties(entity, vendor);

			model.addAttribute("vendor", vendor);

			return new ModelAndView("Admin/vendor/edit", model);
		}
		return new ModelAndView("forward:/Admin/vendor", model);
	}

	public boolean checkExistPhone(ModelMap model, Vendor vendor, int id) {
		if (vendorService.findByPhone(vendor.getPhone()) != null) {
			if (id != 0) {
				if (vendorService.findByPhone(vendor.getPhone()).getId() != id) {
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

	public boolean checkExistEmail(ModelMap model, Vendor vendor, int id) {
		if (vendorService.findByEmail(vendor.getEmail()) != null) {
			if (id != 0) {
				if (vendorService.findByEmail(vendor.getEmail()).getId() != id) {
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
		if (vendorService.findByUsername(account.getUsername()) != null) {
			model.addAttribute("existUsername", "USERNAME NÀY ĐÃ TỒN TẠI!");
			return true;
		}
		return false;
	}

	public boolean checkPhoneValid(ModelMap model, Vendor vendor) {
		String phoneRegex = "^\\d{10}$";
		if (!Pattern.matches(phoneRegex, vendor.getPhone())) {
			model.addAttribute("checkPhone", "SỐ ĐIỆN THOẠI PHẢI CÓ 10 SỐ!");
			return true;
		}
		return false;
	}

	public boolean checkSalary(ModelMap model, Vendor vendor) {
		if (vendor.getSalary() < 0) {
			model.addAttribute("valid", "NHẬP VÀO MỘT SỐ LỚN HƠN 0!");
			return true;
		}
		return false;
	}

	@PostMapping("/create")
	public ModelAndView createVendor(ModelMap model, @Valid @ModelAttribute("vendor") Vendor vendor, BindingResult result,
			@Valid @ModelAttribute("account") Account account) {
		
		System.out.println(vendor.getFullname());
		System.out.println(account.toString());
		if (result.hasErrors()) {
			model.addAttribute("vendor", vendor);
			System.out.println(result.toString());
			return new ModelAndView("Admin/vendor/add", model);
		}
		int id = vendor.getId();
		System.out.println(vendor.toString() + account.toString());

		boolean check = false;
		if (checkExistPhone(model, vendor, id) || checkExistEmail(model, vendor, id)
				|| checkExistUsername(model, account, id) || checkPhoneValid(model, vendor)
				|| checkSalary(model, vendor)) {
			check = true;
		}
		if (check) {
			Vendor staff = new Vendor();
			BeanUtils.copyProperties(vendor, staff);
			staff.setAccount(account);
			model.addAttribute("vendor", staff);
			return new ModelAndView("Admin/vendor/add", model);
		}

		Vendor entity = new Vendor();
		Account tk = new Account();
		BeanUtils.copyProperties(vendor, entity);
		BeanUtils.copyProperties(account, tk);
		tk.setActive(1);
		tk.setPassword(passwordEncoder.encode(account.getPassword()));
		entity.setAccount(tk);
		tk.setRole(new Role(3, "VENDOR"));
		entity.setAccount(tk);
		
		vendorService.save(entity);

		return new ModelAndView("forward:/Admin/vendor", model);
	}

	@PostMapping("/edit")
	public ModelAndView editVendor(ModelMap model, @Valid @ModelAttribute Vendor vendor,
			@Valid @ModelAttribute Account account, BindingResult result) {
		if (result.hasErrors()) {
			model.addAttribute("vendor", vendor);
			return new ModelAndView("Admin/vendor/edit", model);
		}
		int id = vendor.getId();

		Vendor oldvendor = vendorService.findById(id).get();
		boolean check = false;
		if (checkExistPhone(model, vendor, id) || checkExistEmail(model, vendor, id) || checkPhoneValid(model, vendor)
				|| checkSalary(model, vendor)) {
			check = true;
		}

		if (check) {
			Vendor staff = new Vendor();
			BeanUtils.copyProperties(vendor, staff);

			model.addAttribute("vendor", staff);

			return new ModelAndView("Admin/vendor/edit", model);

		}
		Account oldAccount = oldvendor.getAccount();
		oldAccount.setActive(account.getActive());
		BeanUtils.copyProperties(vendor, oldvendor);
		oldvendor.setAccount(oldAccount);
		vendorService.save(oldvendor);

		return new ModelAndView("forward:/Admin/vendor", model);
	}

	@Transactional
	@GetMapping("/delete/{id}")
	public ModelAndView delete(ModelMap model, @PathVariable("id") int id) {
		Optional<Vendor> optVendor = vendorService.findById(id);
		String message = "";
		if (optVendor.isPresent()) {

			Account acc = optVendor.get().getAccount();
			Integer accountid = acc.getAccountId();

			vendorService.deleteByAccountId(accountid);

			vendorService.deleteById(id);
			
			message = "Xoá thành công";
			model.addAttribute("message", message);

			List<Vendor> list = vendorService.findAll();
			model.addAttribute("list", list);
			return new ModelAndView("forward:/Admin/vendor", model);
		}
		return new ModelAndView("forward:/Admin/vendor", model);
	}

}
