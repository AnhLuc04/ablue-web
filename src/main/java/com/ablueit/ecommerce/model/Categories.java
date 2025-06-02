package com.ablueit.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Categories extends AbstractEntity<Long> {

    @Column(nullable = false)
    String name;

    @ManyToOne
    @CreatedBy
    User createdBy;

    // Danh mục thuộc về cửa hàng nào
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
