package vn.iotstar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.ProductFeedback;
import vn.iotstar.repository.IProductFeedbackRepository;
import vn.iotstar.service.IProductFeedbackService;

@Service
public class ProductFeedbackService implements IProductFeedbackService{
	@Autowired
	private IProductFeedbackRepository feedbackRepo;

	@Override
	public <S extends vn.iotstar.entity.ProductFeedback> S save(S entity) {
		return feedbackRepo.save(entity);
	}

	@Override
	public List<ProductFeedback> findByProduct_ProductId(int productId) {
		return feedbackRepo.findByProduct_ProductId(productId);
	}

	@Override
	public Page<ProductFeedback> findByProduct_ProductId(int productId, Pageable page) {
		return feedbackRepo.findByProduct_ProductId(productId, page);
	}

	public List<ProductFeedback> findByUserIdAndProduct_ProductId(int id, int productId) {
		return feedbackRepo.findByUserIdAndProduct_ProductId(id, productId);
	}

	@Override
	public Double findAverageRatingByProductId(Long productId) {
		return feedbackRepo.findAverageRatingByProductId(productId);
	}
	
	
}
