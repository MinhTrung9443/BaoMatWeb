package vn.iotstar.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import vn.iotstar.entity.Delivery;
import vn.iotstar.entity.Order;
import vn.iotstar.entity.Shipper;
import vn.iotstar.service.IDeliveryService;
import vn.iotstar.service.IOrderService;
import vn.iotstar.service.IShipperService;

@Controller
@RequestMapping("Admin/delivery")
public class ManageDeliveryController {

	@Autowired
	IDeliveryService deliveryService;

	@Autowired
	IShipperService shipperService;

	@Autowired
	IOrderService orderService;

	@RequestMapping("")
	public String getdelivery(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {

		int count = (int) deliveryService.count();
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);

		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

		int totalPages = deliveryService.findAll(pageable).getTotalPages();
		Page<Delivery> list = deliveryService.findAll(pageable);
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
		return "Admin/delivery/list";
	}

	@GetMapping("/listShipper/{id}")
	public String listShipper(@PathVariable("id") int deliveryId, Model model) {
		Optional<Delivery> optDelivery = deliveryService.findById(deliveryId);
		if (optDelivery.isPresent()) {
			List<Shipper> list = optDelivery.get().getShipper();
			model.addAttribute("list", list);
			return "/Admin/delivery/listShipper";
		}

		return "Admin/delivery";
	}

	@GetMapping("/add")
	public String addDelivery(Model model) {
		Delivery delivery = new Delivery();
		model.addAttribute("delivery", delivery);
		return "Admin/delivery/add";
	}

	@GetMapping("/edit/{id}")
	public String viewUserDetail(@PathVariable("id") int deliveryId, Model model) {
		Optional<Delivery> optDelivery = deliveryService.findById(deliveryId);
		if (optDelivery.isPresent()) {
			Delivery delivery = optDelivery.get();
			model.addAttribute("delivery", delivery);
			return "Admin/delivery/edit";
		}
		return "Admin/delivery";
	}

	public boolean checkExistDelivery(ModelMap model, Delivery delivery, int id) {
		if (deliveryService.findByDeliveryName(delivery.getDeliveryName()) != null) {
			if (id != 0) {
				if (deliveryService.findByDeliveryName(delivery.getDeliveryName()).getDeliveryId() != id) {
					model.addAttribute("existDelivery", "NHÀ VẬN CHUYỂN NÀY ĐÃ TỒN TẠI!");
					return true;
				}
			} else {
				model.addAttribute("existDelivery", "NHÀ VẬN CHUYỂN NÀY ĐÃ TỒN TẠI!");
				return true;
			}
		}
		return false;
	}

	@PostMapping("/save")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute Delivery delivery, BindingResult result) {
		if (result.hasErrors()) {
			model.addAttribute("delivery", delivery);
			if (delivery.getDeliveryId() != 0) {
				return new ModelAndView("Admin/delivery/edit", model);
			} else {
				return new ModelAndView("Admin/delivery/add", model);
			}
		}
		boolean check = false;
		if (checkExistDelivery(model, delivery, delivery.getDeliveryId())) {
			check = true;
		}
		if (check) {
			Delivery deli = new Delivery();
			BeanUtils.copyProperties(delivery, deli);
			model.addAttribute("delivery", deli);
			if (delivery.getDeliveryId() != 0) {
				return new ModelAndView("Admin/delivery/edit", model);
			} else {
				return new ModelAndView("Admin/delivery/add", model);
			}
		}
		Delivery entity = new Delivery();
		BeanUtils.copyProperties(delivery, entity);

		deliveryService.save(entity);
		return new ModelAndView("forward:/Admin/delivery", model);
	}

	@Transactional
	@GetMapping("/delete/{id}")
	public ModelAndView delete(ModelMap model, @PathVariable("id") int deliveryId) {
		Optional<Delivery> optDelivery = deliveryService.findById(deliveryId);
		String message = "";
		if (optDelivery.isPresent()) {

			Delivery delivery = optDelivery.get();
			List<Order> listOrder = orderService.findByDelivery_DeliveryId(deliveryId);
			if (listOrder != null && !listOrder.isEmpty()) {
				message = "Không thể xóa vì nhà vận chuyển này đang được sử dụng trong Order";
				model.addAttribute("message", message);
				List<Delivery> list = deliveryService.findAll();
				model.addAttribute("list", list);
				return new ModelAndView("Admin/delivery/list", model);
			}
			List<Shipper> listShipper = delivery.getShipper();
			for (Shipper sh : listShipper) {
				sh.setDelivery(null);
				shipperService.save(sh);
			}

			deliveryService.deleteById(deliveryId);
			message = "Xoá thành công";
			model.addAttribute("message", message);

			List<Delivery> list = deliveryService.findAll();
			model.addAttribute("list", list);
			return new ModelAndView("Admin/delivery/list", model);
		}
		return new ModelAndView("forward:/Admin/delivery", model);
	}

}
