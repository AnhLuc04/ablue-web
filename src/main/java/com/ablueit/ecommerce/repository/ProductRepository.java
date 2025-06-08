package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.payload.request.VariantRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);


    Optional<Product> findById(Long aLong);
    Page<Product> findByStoreId(Long storeId, Pageable pageable);
    Optional<Product> findBySku(String sku);
}