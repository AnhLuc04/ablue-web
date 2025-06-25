package com.ablueit.ecommerce.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

@Entity
public class OrderTaxLine {
    @Id
    @GeneratedValue
    private Long id;
    private String rateCode;
    private Long rateId;
    private String label;
    private boolean compound;
    private BigDecimal taxTotal;
    private BigDecimal shippingTaxTotal;

    @ManyToOne
    private Order order;
}