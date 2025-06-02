//package com.ablueit.ecommerce.model;
//
//
//import com.fasterxml.jackson.annotation.JsonManagedReference;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Cart {
//    @Id @GeneratedValue
//    private Long id;
//
//    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CartItem> items = new ArrayList<>();
//
//    @OneToOne
//    private User user;
//
//    // tính tổng giá trị giỏ hàng
//    public double getTotal() {
//        return items.stream().mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity()).sum();
//    }
//
//    // thêm/xoá sản phẩm
//}






package com.ablueit.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nếu có hệ thống người dùng, có thể thêm:
     @OneToOne
     private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public double getTotal() {
        return items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    public void addItem(Variation variant, int quantity) {
        for (CartItem item : items) {
            if (item.getVariation().getId().equals(variant.getId())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        CartItem newItem = new CartItem();
        newItem.setCart(this);
        newItem.setVariation(variant);
        newItem.setQuantity(quantity);
        items.add(newItem);
    }
}
