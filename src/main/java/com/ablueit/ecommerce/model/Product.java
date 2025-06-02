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
@Table(name = "product")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Product extends AuditEntity<Long> {

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "slug")
    String slug;

    @Column(name = "permalink")
    String permalink;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    ProductStatus status = ProductStatus.PUBLISHED;

    @Column(name = "description", columnDefinition = "text")
    String description;

    @Column(name = "short_description")
    String shortDescription;

    @Column(name = "sku")
    String sku;

    @Column(name = "price")
    Double price; // current product price

    @Column(name = "regular_price")
    Double regularPrice; // product regular price

    @Column(name = "sale_price")
    Double salePrice;

    @Column(name = "date_start_sale")
    LocalDateTime dateStartSale;

    @Column(name = "date_end_sale")
    LocalDateTime dateEndSale;

    @Column(name = "is_on_sale")
    Boolean isOnSale;

    @Column(name = "total_sale")
    Integer totalSale;

    @Column(name = "stock_quantity")
    Integer stockQuantity;

    @Column(name = "stock_status")
    StockStatus stockStatus = StockStatus.IN_STOCK;

    @Column(name = "back_order_allowed")
    Boolean backOrderAllowed;

    @Column(name = "weight")
    String weight;

    @Column(name = "average_rating")
    String averageRating;

    @Column(name = "rating_count")
    Integer ratingCount;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<ProductImage> productImages;

    @OneToMany(mappedBy = "product")
    private List<Categories> categories;


//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    List<Variation> variations = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "store_id")
    Store store;

    public Product(Long id) {
        this.id = id;
    }
}