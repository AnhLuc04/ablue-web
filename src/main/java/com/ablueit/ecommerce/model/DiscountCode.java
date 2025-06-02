package com.ablueit.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private double discountAmount; // số tiền giảm hoặc phần trăm giảm

    private boolean percentage; // true = %, false = tiền mặt

    private boolean active;

    // 🧑‍💻 Mối quan hệ với người dùng
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
