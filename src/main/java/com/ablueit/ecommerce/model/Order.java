package com.ablueit.ecommerce.model;


import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Người đặt hàng
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    // Thuộc cửa hàng nào
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    private String status; // PENDING, SHIPPED, COMPLETED

    private BigDecimal totalAmount;
    @OneToOne(cascade = CascadeType.ALL)
    private BillingAddress billingAddress;

    @OneToOne(cascade = CascadeType.ALL)
    private ShippingAddress shippingAddress;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;

}
