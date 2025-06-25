package com.ablueit.ecommerce.service.impl;

import com.ablueit.ecommerce.model.CodeCoupon;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.repository.CodeCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CodeCouponRepository codeCouponRepository;

    public Optional<CodeCoupon> findValidCoupon(String code, User user, double orderTotal) {
        LocalDateTime now = LocalDateTime.now();
        return codeCouponRepository.findByCodeIgnoreCase(code)
                .filter(coupon -> coupon.isValid(now))
                .filter(coupon -> coupon.getMinOrderValue() == null || orderTotal >= coupon.getMinOrderValue())
                .filter(coupon ->
                        CollectionUtils.isEmpty(coupon.getAllowedUsers()) ||
                                coupon.getAllowedUsers().contains(user)
                )
                ;
    }





    public List<CodeCoupon> findActivePublicCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return codeCouponRepository.findByAllowedUsersIsEmptyAndActiveTrueAndStartDateBeforeAndEndDateAfter(now, now);
    }


    public List<CodeCoupon> findUserCoupons(User user) {
        LocalDateTime now = LocalDateTime.now();
        return codeCouponRepository.findByAllowedUsersContainingAndActiveTrueAndStartDateBeforeAndEndDateAfter(
                user, now, now
        );
    }


    public double calculateDiscount(CodeCoupon coupon, double subtotal) {
        if (!coupon.isValid(LocalDateTime.now())) return 0.0;

        if (coupon.getDiscountType() == CodeCoupon.DiscountType.PERCENT) {
            return subtotal * (coupon.getDiscountValue() / 100.0);
        } else if (coupon.getDiscountType() == CodeCoupon.DiscountType.AMOUNT) {
            return coupon.getDiscountValue();
        }
        return 0.0;
    }
}
