package com.ablueit.ecommerce.model;

import com.ablueit.ecommerce.enums.ProductStatus;
import com.ablueit.ecommerce.enums.StockStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "variation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Variation extends AuditEntity<Long> {

    @Id
    @Column(name = "variation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "description")
    String description;

    @Column(name = "permalink")
    String permalink;

    @Column(name = "sku")
    String sku;

    @Column(name = "price")
    Double price;

    @Column(name = "regular_price")
    Double regularPrice;

    @Column(name = "sale_price")
    Double salePrice;

    @Column(name = "date_start_sale")
    LocalDateTime dateStartSale;

    @Column(name = "date_end_sale")
    LocalDateTime dateEndSale;

    @Column(name = "is_on_sale")
    Boolean isOnSale;

    @Column(name = "status")
    ProductStatus status = ProductStatus.PUBLISHED;

    @Column(name = "total_sale")
    Integer totalSale;

    @Column(name = "stock_quantity")
    Integer stockQuantity;

    @Column(name = "stock_status")
    StockStatus stockStatus = StockStatus.IN_STOCK;

    @Column(name = "back_order_allowed")
    Boolean backOrderAllowed;

    @Column(name = "weight")
    Double weight;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "product_id")
    Product product;

    @OneToMany(mappedBy = "variation", cascade = CascadeType.ALL, orphanRemoval = true)
    List<VariationAttribute> attributes = new ArrayList<>();
}
