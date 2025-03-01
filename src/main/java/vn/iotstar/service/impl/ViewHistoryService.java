package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.ViewHistory;
import vn.iotstar.repository.IViewHistoryRepository;
import vn.iotstar.service.IViewHistoryService;

@Service
public class ViewHistoryService implements IViewHistoryService{
	@Autowired
	private IViewHistoryRepository viewrepo;

	@Override
	public <S extends ViewHistory> S save(S entity) {
		return viewrepo.save(entity);
	}

	@Override
	public void deleteById(Integer id) {
		viewrepo.deleteById(id);
	}

	@Override
	public Page<ViewHistory> findAllByUserId(int id, Pageable pageable) {
		return viewrepo.findAllByUserId(id, pageable);
	}

	@Override
	public int countAllByUserId(int id) {
		return viewrepo.countAllByUserId(id);
	}

	@Override
	public ViewHistory findByUserIdAndProduct_ProductId(int id, int productId) {
		return viewrepo.findByUserIdAndProduct_ProductId(id, productId);
	}
	
	
}
