package vn.iotstar.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;

@Controller
@RequestMapping("/Admin/category")
public class ManageCategoryController {

	@Autowired
	ICategoryService cateService;

	@RequestMapping("")
	public String home(Model model, @RequestParam("page") Optional<Integer> page,
			@RequestParam("size") Optional<Integer> size) {

		int count = (int) cateService.count();
		int currentPage = page.orElse(1);
		int pageSize = size.orElse(5);

		Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

		int totalPages = cateService.findAll(pageable).getTotalPages();
		Page<Category> list = cateService.findAll(pageable);
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
		return "Admin/category/list";
	}

	@GetMapping("/add")
	public String addCategory(Model model) {
		Category category = new Category();
		model.addAttribute("cate", category);
		return "Admin/category/add";
	}

	public boolean checkExistCategory(ModelMap model, Category category, int id) {
		if (cateService.findByCategoryName(category.getCategoryName()) != null) {
			if (id != 0) {
				if (cateService.findByCategoryName(category.getCategoryName()).getCategoryId() != id) {
					model.addAttribute("existCateName", "DANH MỤC NÀY ĐÃ TỒN TẠI!");
					return true;
				}
			} else {
				model.addAttribute("existCateName", "DANH MỤC NÀY ĐÃ TỒN TẠI!");
				return true;
			}

		}
		return false;
	}

	@PostMapping("/save")
	public ModelAndView addOrEdit(ModelMap model, @Valid @ModelAttribute Category category, BindingResult result) {
		if (result.hasErrors()) {
			model.addAttribute("cate", category);
			if (category.getCategoryId() != 0) {
				return new ModelAndView("Admin/category/edit", model);
			} else {
				return new ModelAndView("Admin/category/add", model);
			}
		}

		boolean check = false;
		if (checkExistCategory(model, category, category.getCategoryId())) {
			check = true;
		}
		if (check) {
			Category cate = new Category();
			BeanUtils.copyProperties(category, cate);
			model.addAttribute("cate", cate);
			if (category.getCategoryId() != 0) {
				return new ModelAndView("Admin/category/edit", model);
			} else {
				return new ModelAndView("Admin/category/add", model);
			}
		}
		Category entity = new Category();
		BeanUtils.copyProperties(category, entity);

		cateService.save(entity);
		return new ModelAndView("forward:/Admin/category", model);
	}

	@GetMapping("/edit/{id}")
	public ModelAndView edit(ModelMap model, @PathVariable("id") Integer categoryId) {
		Optional<Category> optEmployee = cateService.findById(categoryId);
		Category category = new Category();
		if (optEmployee.isPresent()) {
			Category entity = optEmployee.get();

			BeanUtils.copyProperties(entity, category);

			model.addAttribute("cate", category);

			return new ModelAndView("Admin/category/edit", model);
		}
		return new ModelAndView("forward:/Admin/category", model);
	}

}
