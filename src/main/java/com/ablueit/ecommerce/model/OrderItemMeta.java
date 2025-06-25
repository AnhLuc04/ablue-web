package com.ablueit.ecommerce.model;

import jakarta.persistence.*;

@Entity
public class OrderItemMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meta_key") // ✅ đổi tên cột
    private String key;

    @Column(name = "meta_value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;
}
