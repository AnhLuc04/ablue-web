package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.enums.ImageType;
import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    ProductImage findByProductAndImageType(Product product, ImageType type);

    List<ProductImage> findAllByProductAndImageType(Product product, ImageType type);
}
