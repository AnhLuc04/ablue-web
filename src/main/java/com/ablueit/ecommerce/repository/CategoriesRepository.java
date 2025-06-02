package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.Categories;
import com.ablueit.ecommerce.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {

    boolean existsByName(String name);

    List<Categories> findByStore(Store store);

    Optional<Categories> findById(Long id);

    Optional<Categories> findByName(String name);
}
