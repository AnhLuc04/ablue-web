package com.ablueit.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//
//
//public class OrderItem {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    Order order;
//
//    @ManyToOne
//    @JoinColumn(name = "variation_id")
//    Variation variation;
//
//    int quantity;
//    double price;
//    double totalPrice; // price * quantity
//}




public class OrderItem {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long productId;
    private Long variationId;
    private int quantity;
    private String taxClass;
    private BigDecimal subtotal;
    private BigDecimal subtotalTax;
    private BigDecimal total;
    private BigDecimal totalTax;
    private String sku;
    private BigDecimal price;

    @ManyToOne
    private Order order;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
    private List<OrderItemMeta> metaData = new ArrayList<>();
}
