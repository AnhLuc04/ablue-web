package com.ablueit.ecommerce.model;

import com.ablueit.ecommerce.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CodeCoupon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String description;
    private Double discountValue; // 10.0 = 10% hoặc 10000 = 10,000đ tùy loại

    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // PERCENT, AMOUNT

    private Double minOrderValue;
    private Integer maxUsageCount;
    private Integer usedCount = 0;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @ManyToMany
    @JoinTable(
            name = "coupon_user",
            joinColumns = @JoinColumn(name = "coupon_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> allowedUsers = new HashSet<>();


    private Boolean active;

    public boolean isValid(LocalDateTime now) {
        return Boolean.TRUE.equals(active)
                && (startDate == null || !now.isBefore(startDate))
                && (endDate == null || !now.isAfter(endDate))
                && (maxUsageCount == null || usedCount < maxUsageCount);
    }

    public enum DiscountType {
        PERCENT,
        AMOUNT
    }
}
