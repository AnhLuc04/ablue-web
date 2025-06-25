package com.ablueit.ecommerce.model;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;

@Entity
public class OrderRefund {
    @Id
    private Long id;
    private String refund;
    private BigDecimal total;

    @ManyToOne
    private Order order;
}
