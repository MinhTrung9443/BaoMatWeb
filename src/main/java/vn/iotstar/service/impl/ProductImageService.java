package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.ProductImage;
import vn.iotstar.repository.IProductImageRepository;


@Service
public class ProductImageService {

	@Autowired
    private IProductImageRepository productImageRepository;

    // Phương thức lưu hình ảnh vào cơ sở dữ liệu
    public void save(ProductImage productImage) {
        productImageRepository.save(productImage);
    }

}
