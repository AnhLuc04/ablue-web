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

    // ➤ Mỗi giỏ hàng của một người dùng (One-to-One)
    @OneToOne
    private User user;

    // ➤ Mỗi Cart thuộc về một Store
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    // ➤ Một Cart có nhiều sản phẩm
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    // ➤ Tính tổng tiền
    public double getTotal() {
        return items.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }

    // ➤ Thêm item
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

    // ➤ Xóa item theo variantId
    public void removeItemByVariantId(Long variantId) {
        items.removeIf(item -> item.getVariation().getId().equals(variantId));
    }
}
