package com.ablueit.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingMethod {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;           // e.g., "Standard Shipping"
    private String carrier;        // e.g., "GHN"
    private Double fee;
    private Integer estimatedDays; // e.g., 3 (3-5 working days)

    private Boolean active;
}
