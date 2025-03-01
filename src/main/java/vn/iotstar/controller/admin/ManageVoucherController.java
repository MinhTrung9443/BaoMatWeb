package vn.iotstar.controller.admin;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import vn.iotstar.dto.VoucherDTO;
import vn.iotstar.entity.Voucher;
import vn.iotstar.service.IVoucherService;

@Controller
@RequestMapping("/Admin/voucher")
public class ManageVoucherController {

	@Autowired
	IVoucherService voucherService;

	@RequestMapping("")
	public String home(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {

		int count = (int) voucherService.count();
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);

		Pageable pageable = PageRequest.of(currentPage - 1, pageSize,Sort.by(Order.desc("active")));

		int totalPages = voucherService.findAll(pageable).getTotalPages();
		Page<Voucher> list = voucherService.findAll(pageable);
		int start = Math.max(1, currentPage - 2);
		int end = Math.min(currentPage + 2, totalPages);

		if (totalPages > count) {
			if (end == totalPages)
				start = end - count;
			else if (start == 1)
				end = start + count;
		}

		List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

		model.addAttribute("list", list);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("pageNumbers", pageNumbers);
		return "Admin/voucher/list";
	}

	@GetMapping("/add")
	public String add(Model model) {
		VoucherDTO voucherDTO = new VoucherDTO();
		model.addAttribute("voucherDTO", voucherDTO);
		return "Admin/voucher/add";
	}

	@GetMapping("/edit/{id}")
	public ModelAndView edit(ModelMap model, @PathVariable("id") Integer voucherId) {
		Optional<Voucher> optVoucher = voucherService.findById(voucherId);
		Voucher voucher = new Voucher();
		if (optVoucher.isPresent()) {
			Voucher entity = optVoucher.get();
			BeanUtils.copyProperties(entity, voucher);
			model.addAttribute("voucher", voucher);
			return new ModelAndView("Admin/voucher/edit", model);
		}
		return new ModelAndView("forward:/Admin/voucher", model);
	}

	// Kiểm tra mã code đã tồn tại chưa
	public boolean existCode(ModelMap model, VoucherDTO voucherDTO, int id) {
		boolean temp = false;
		if (voucherService.existsByVoucherCode(voucherDTO.getVoucherCode())) {
			if (id != 0) {
				List<Voucher> list = voucherService.findByVoucherCode(voucherDTO.getVoucherCode());
				for (Voucher v : list) {
					if (v.getVoucherId() == id) {
						temp = true;
						break;
					}
				}
				if (!temp) {
					model.addAttribute("existCode", "MÃ CODE ĐÃ TỒN TẠI!!!");
					return true;
				}

			} else {
				model.addAttribute("existCode", "MÃ CODE ĐÃ TỒN TẠI!!!");
				return true;
			}
		}
		return false;
	}

	// Kiểm tra startDate, endDate
	public boolean isValidDate(ModelMap model, VoucherDTO voucherDTO) {
		if (voucherDTO.getStartDate() != null & voucherDTO.getEndDate() != null) {
			LocalDate startDay = voucherDTO.getStartDate().toLocalDate();
			LocalDate endDay = voucherDTO.getEndDate().toLocalDate();

			LocalTime startTime = voucherDTO.getStartDate().toLocalTime();
			LocalTime endTime = voucherDTO.getEndDate().toLocalTime();

			if (startDay.isEqual(endDay)) {

				if (startTime.isAfter(endTime)) {
					model.addAttribute("checkDate", "GIỜ BẮT ĐẦU PHẢI TRƯỚC GIỜ KẾT THÚC!");
					return true;
				} else if (startTime.equals(endTime)) {
					model.addAttribute("checkDate", "NGÀY GIỜ BẮT ĐẦU KHÔNG ĐƯỢC TRÙNG NGÀY GIỜ KẾT THÚC!");
					return true;
				}
			} else if (voucherDTO.getStartDate().isAfter(voucherDTO.getEndDate())) {
				model.addAttribute("checkDate", "NGÀY BẮT ĐẦU PHẢI TRƯỚC NGÀY KẾT THÚC!");
				return true;
			}
		}
		return false;
	}

	// Kiểm tra số lượng voucher thêm vào
	public boolean checkQuantity(ModelMap model, VoucherDTO voucherDTO) {
		if (voucherDTO.getQuantity() <= 0) {
			model.addAttribute("valid", "SỐ LƯỢNG PHẢI LỚN HƠN 0!!!");
			return true;
		}
		return false;
	}

	public boolean checkMinimumCost(ModelMap model, VoucherDTO voucherDTO) {
		if (voucherDTO.getMinimumCost() < 0) {
			model.addAttribute("checkMininumCost", "GIÁ TRỊ ĐƠN HÀNG PHẢI LỚN HƠN 0!!!");
			return true;
		}
		return false;
	}

	@PostMapping("/save")
	public ModelAndView addOrEdit(ModelMap model, @Valid @ModelAttribute Voucher voucher,
			@Valid @ModelAttribute VoucherDTO voucherDTO, BindingResult result) {
		if (result.hasErrors()) {
			if (voucher.getVoucherId() != 0) {
				return new ModelAndView("Admin/voucher/edit", model);
			} else {
				return new ModelAndView("Admin/voucher/add", model);
			}
		}

		boolean check = false;

		// Kiểm tra nếu voucherId đã tồn tại thì sửa ngược lại thêm
		if (voucherDTO.getVoucherId() != 0) {

			Optional<Voucher> optionalEntity = voucherService.findById(voucher.getVoucherId());

			Voucher entity = optionalEntity.get();

			if (existCode(model, voucherDTO, voucher.getVoucherId())) {
				check = true;
			}

			if (isValidDate(model, voucherDTO)) {
				check = true;
			}

			if (checkMinimumCost(model, voucherDTO)) {
				check = true;
			}

			if (check) {
				return new ModelAndView("Admin/voucher/edit", model);
			}

			String name = entity.getVoucherCode();

			List<Voucher> listByVoucherCode = voucherService.findByVoucherCode(name);

			for (Voucher mgg : listByVoucherCode) {

				mgg.setVoucherCode(voucher.getVoucherCode());
				mgg.setVoucherType(voucher.getVoucherType());
				mgg.setVoucherValue(voucher.getVoucherValue());
				mgg.setMinimumCost(voucher.getMinimumCost());
				mgg.setStartDate(voucher.getStartDate());
				mgg.setEndDate(voucher.getEndDate());
				mgg.setActive(voucher.getActive());
				voucherService.save(mgg);
			}
		} else {
			int quantity = voucherDTO.getQuantity();

			if (existCode(model, voucherDTO, voucherDTO.getVoucherId())) {
				check = true;
			}

			if (checkQuantity(model, voucherDTO)) {
				check = true;
			}

			if (isValidDate(model, voucherDTO)) {
				check = true;
			}

			if (checkMinimumCost(model, voucherDTO)) {
				check = true;
			}
			if (check) {
				return new ModelAndView("Admin/voucher/add", model);
			}

			for (int i = 0; i < quantity; i++) {
				Voucher newVoucher = new Voucher();
				BeanUtils.copyProperties(voucherDTO, newVoucher);
				voucherService.save(newVoucher);
			}
		}
		return new ModelAndView("forward:/Admin/voucher", model);
	}

	@Transactional
	@GetMapping("/delete/{id}")
	public ModelAndView delete(ModelMap model, @PathVariable("id") int voucherId,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
		Optional<Voucher> optVoucher = voucherService.findById(voucherId);
		 String message = "";
		if (optVoucher.isPresent()) {
			voucherService.deleteById(voucherId);
			message = "Xoá thành công";
			model.addAttribute("message", message);
		}
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);

		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);
		Page<Voucher> list = voucherService.findAll(pageable);
		
		int count = (int) voucherService.count();
		int totalPages = voucherService.findAll(pageable).getTotalPages();
		int start = Math.max(1, currentPage - 2);
		int end = Math.min(currentPage + 2, totalPages);

		if (totalPages > count) {
			if (end == totalPages)
				start = end - count;
			else if (start == 1)
				end = start + count;
		}

		List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());

		model.addAttribute("list", list);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("pageNumbers", pageNumbers);
		return new ModelAndView("Admin/voucher/list", model);
	}

}
