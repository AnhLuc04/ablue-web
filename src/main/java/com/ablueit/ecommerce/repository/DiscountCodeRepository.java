package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {
    List<DiscountCode> findByActiveTrueAndUserUsername(String username);

    List<DiscountCode> findByActiveTrue();
}
