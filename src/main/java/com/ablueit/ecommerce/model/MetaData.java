package com.ablueit.ecommerce.model;

import jakarta.persistence.*;

@Entity
@Table(name = "meta_data")
public class MetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meta_key")
    private String metaKey;

    @Column(name = "meta_value")
    private String metaValue;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
