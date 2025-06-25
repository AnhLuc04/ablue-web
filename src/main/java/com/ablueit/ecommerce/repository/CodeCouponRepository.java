package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.CodeCoupon;
import com.ablueit.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CodeCouponRepository extends JpaRepository<CodeCoupon,Long> {
    Optional<CodeCoupon> findByCodeIgnoreCase(String code);

    // Mã công khai (không gán cho user nào)
    List<CodeCoupon> findByAllowedUsersIsEmptyAndActiveTrueAndStartDateBeforeAndEndDateAfter(
            LocalDateTime start, LocalDateTime end
    );

    // Mã áp dụng riêng cho một user cụ thể
    List<CodeCoupon> findByAllowedUsersContainingAndActiveTrueAndStartDateBeforeAndEndDateAfter(
            User user, LocalDateTime start, LocalDateTime end
    );

}
