package vn.iotstar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Category;
import vn.iotstar.repository.ICategoryRepository;
import vn.iotstar.service.ICategoryService;

@Service
public class CategoryService implements ICategoryService{
	@Autowired
	private ICategoryRepository cateRepository;
	

	public CategoryService(ICategoryRepository cateRepository) {
		this.cateRepository = cateRepository;
	}

	@Override
	public long count() {
		return cateRepository.count();
	}

	@Override
	public Optional<Category> findById(int id) {
		return cateRepository.findById(id);
	}
	@Override
	public List<Category> findAllById(int id) {
		return cateRepository.findByCategoryId(id);
	}

	@Override
	public List<Category> findAll() {
		return cateRepository.findAll();
	}

	@Override
	public Page<Category> findAll(Pageable pageable) {
		return cateRepository.findAll(pageable);
	}

	@Override
	public <S extends Category> S save(S entity) {
		return cateRepository.save(entity);
	}

	@Override
	public void deleteById(Integer id) {
		cateRepository.deleteById(id);
	}

	@Override
	public Category findByCategoryName(String categoryName) {
		return cateRepository.findByCategoryName(categoryName);
	}



    public List<Category> getAllCategories() {
        return cateRepository.findAll();
    }
	public List<Category> getActiveCategories() {
		 return cateRepository.findByActive(1);
	}
}
