package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.Attribute;
import com.ablueit.ecommerce.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute,Long> {
    // Tìm thuộc tính theo tên
    Optional<Attribute> findByName(String name);

    Boolean existsByName(String name);
}
