//package com.ablueit.ecommerce.model;
//
//
//import jakarta.persistence.*;
//import lombok.*;
//import java.math.BigDecimal;
//import java.util.List;
//
//@Entity
//@Table(name = "orders")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Order {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // Người đặt hàng
//    @ManyToOne
//    @JoinColumn(name = "customer_id")
//    private User customer;
//
//    // Thuộc cửa hàng nào
//    @ManyToOne
//    @JoinColumn(name = "store_id")
//    private Store store;
//
//    private String status; // PENDING, SHIPPED, COMPLETED
//
//    private BigDecimal totalAmount;
//    @OneToOne(cascade = CascadeType.ALL)
//    private BillingAddress billingAddress;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    private ShippingAddress shippingAddress;
//
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    private List<OrderItem> items;
//
//}








package com.ablueit.ecommerce.model;

import com.paypal.api.payments.Billing;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import com.ablueit.ecommerce.model.CodeCoupon;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "orders")
//public class Order {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    Long id;
//    @CreationTimestamp
//    @Column(updatable = false)
//    LocalDateTime createdAt;
//
//    @UpdateTimestamp
//    LocalDateTime updatedAt;
//
//    // Người đặt hàng
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    User user;
//
//    // Sản phẩm trong đơn
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
//    List<OrderItem> items;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "shipping_address_id")
//    private ShippingAddress shippingAddress;
//
//    // Mã giảm giá áp dụng
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "coupon_id")
//   CodeCoupon coupon;
//
//    // Phương thức giao hàng
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "shipping_method_id")
//    ShippingMethod shippingMethod;
//
//    // Thông tin PayPal hoặc cổng thanh toán khác
//    String paymentMethod;      // e.g., "paypal", "cod", "stripe"
//    String paymentStatus;      // e.g., "pending", "paid", "failed"
//    String transactionId;
//
//    // Tổng tiền các phần
//    Double subtotal;
//    Double shippingFee;
//    Double discount;
//    Double total;
//
//    // Trạng thái đơn hàng
//    @Enumerated(EnumType.STRING)
//    OrderStatus status;
//
//    LocalDateTime orderDate;
//
//    // Nhật ký trạng thái đơn hàng
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
//    List<OrderLog> logs;
//
//
//}





public class Order {
    @Id @GeneratedValue
    private Long id;
    @ManyToOne
    private User user;
    private Long parentId;
    private String number;
    private String orderKey;
    private String createdVia;
    private String version;
    private String status;
    private String currency;
    private LocalDateTime dateCreated;
    private LocalDateTime dateCreatedGmt;
    private LocalDateTime dateModified;
    private LocalDateTime dateModifiedGmt;
    private BigDecimal discountTotal;
    private BigDecimal discountTax;
    private BigDecimal shippingTotal;
    private BigDecimal shippingTax;
    private BigDecimal cartTax;
    private BigDecimal total;
    private BigDecimal totalTax;
    private boolean pricesIncludeTax;
    private Long customerId;
    private String customerIpAddress;
    private String customerUserAgent;
    private String customerNote;
    private String paymentMethod;
    private String paymentMethodTitle;
    private String transactionId;
    private LocalDateTime datePaid;
    private LocalDateTime datePaidGmt;
    private LocalDateTime dateCompleted;
    private LocalDateTime dateCompletedGmt;
    private String cartHash;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "billing_address_id")
    private BillingAddress billing;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "shipping_address_id")
    private ShippingAddress shipping;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> lineItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderTaxLine> taxLines = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderShippingLine> shippingLines = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderRefund> refunds = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<MetaData> metaData = new ArrayList<>();
}