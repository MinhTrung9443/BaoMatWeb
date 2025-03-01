package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.ViewHistory;

@Repository
public interface IViewHistoryRepository extends JpaRepository<ViewHistory, Integer>{
	ViewHistory findByUserIdAndProduct_ProductId(int id, int productId);
	Page<ViewHistory> findAllByUserId(int id, Pageable pageable);
	int countAllByUserId(int id);
}
