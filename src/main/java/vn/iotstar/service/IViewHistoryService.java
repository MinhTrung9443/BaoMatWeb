package vn.iotstar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.ViewHistory;

@Service
public interface IViewHistoryService {

	void deleteById(Integer id);

	<S extends ViewHistory> S save(S entity);

	int countAllByUserId(int id);

	Page<ViewHistory> findAllByUserId(int id, Pageable pageable);

	ViewHistory findByUserIdAndProduct_ProductId(int id, int productId);

}
