package vn.iotstar.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.ProductFeedback;

@Service
public interface IProductFeedbackService {

	<S extends vn.iotstar.entity.ProductFeedback> S save(S entity);

	Page<ProductFeedback> findByProduct_ProductId(int productId, Pageable page);

	List<ProductFeedback> findByProduct_ProductId(int productId);
	
	Double findAverageRatingByProductId(Long productId);

}
