package vn.iotstar.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Favourite;
import vn.iotstar.repository.IFavouriteRepository;
import vn.iotstar.service.IFavouriteService;


@Service
public class FavouriteService implements IFavouriteService{
	@Autowired
	IFavouriteRepository favourepo;

	@Override
	public Optional<Favourite> findByUserId(int userId, int productId) {
		return favourepo.findByUserIdAndProduct_ProductId(userId,productId);
	}

	@Override
	public <S extends Favourite> S save(S entity) {
		return favourepo.save(entity);
	}

	@Override
	public void deleteById(Integer id) {
		favourepo.deleteById(id);
	}


	@Override
	public Page<Favourite> findAllByUserId(int Id, Pageable page) {
		return favourepo.findAllByUserId(Id, page);
	}

	@Override
	public int countAllByUserId(int id) {
		return favourepo.countAllByUserId(id);
	}
	
	
}
