package com.ablueit.ecommerce.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class OrderShippingLine {
    @Id
    @GeneratedValue
    private Long id;
    private String methodTitle;
    private String methodId;
    private BigDecimal total;
    private BigDecimal totalTax;

    @ManyToOne
    private Order order;

    @OneToMany(mappedBy = "shippingLine", cascade = CascadeType.ALL)
    private List<OrderShippingMeta> metaData = new ArrayList<>();
}
