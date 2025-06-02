package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.Cart;
import com.ablueit.ecommerce.model.DiscountCode;
import com.ablueit.ecommerce.repository.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DiscountService {

    @Autowired
    private DiscountCodeRepository discountRepo;
//
//    public Optional<DiscountCode> validateCode(String code) {
//        return discountRepo.findByCodeAndActiveTrue(code.trim().toUpperCase());
//    }

    public double applyDiscount(Cart cart, DiscountCode discountCode) {
        double total = cart.getTotal();

        if (discountCode.isPercentage()) {
            return total * (1 - discountCode.getDiscountAmount() / 100);
        } else {
            return Math.max(0, total - discountCode.getDiscountAmount());
        }
    }
}
