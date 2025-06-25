package com.ablueit.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "stores")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Store extends AbstractEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "template_id")
    private Template template;
    @Column(unique = true, nullable = false)
    String name;
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();

    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    String phone;

    @Column(nullable = false)
    String address;
    @Column(unique = true, nullable = false)
    String domain; // VD: store1.localhost hoặc aodep.com

    @ManyToOne
    User createdBy;

    Boolean enabled = true;
    // ✅ Thêm trường mới để chọn template/giao diện
//    private String templateName;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private User seller;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Categories> categories;
}
