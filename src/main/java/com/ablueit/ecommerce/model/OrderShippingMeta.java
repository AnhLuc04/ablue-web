package com.ablueit.ecommerce.model;

import jakarta.persistence.*;

@Entity
@   Table(name = "order_shipping_meta")
public class OrderShippingMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String metaKey;
    private String metaValue;

    @ManyToOne
    @JoinColumn(name = "shipping_line_id")
    private OrderShippingLine shippingLine;
}
