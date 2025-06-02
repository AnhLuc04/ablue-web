package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.*;
import com.ablueit.ecommerce.payload.request.CartItemRequest;
import com.ablueit.ecommerce.payload.request.CategoryRequest;
import com.ablueit.ecommerce.payload.request.CheckoutItemView;
import com.ablueit.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    ProductRepository productRepository;

//    @Autowired
//    VariantRepository variantRepository;

    @Autowired
    VariationRepository variationRepository;

    public void addToCart(User user, Long variationId, int quantity) {
        Cart cart = getCartForUser(user);

        Optional<CartItem> existing = cart.getItems().stream()
                .filter(item -> item.getVariation().getId().equals(variationId))
                .findFirst();

        if (existing.isPresent()) {
            existing.get().setQuantity(existing.get().getQuantity() + quantity);
        } else {
            Variation variation = variationRepository.findById(variationId)
                    .orElseThrow(() -> new RuntimeException("Variation not found"));

            CartItem newItem = new CartItem();
            newItem.setVariation(variation);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);

            cart.getItems().add(newItem);
        }

        cartRepository.save(cart);
    }

    public BigDecimal calculateTotal(Cart cart) {
        if (cart == null || cart.getItems() == null) return BigDecimal.ZERO;

        return cart.getItems().stream()
                .map(item -> {
                    double price = item.getVariation() != null ? item.getVariation().getPrice() : 0.0;
                    return BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Cart getCartForUser(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }


//    public void addToCart(User user, Long variantId, int quantity) {
//        Cart cart = getCartForUser(user);
//
//        Optional<CartItem> existing = cart.getItems().stream()
//                .filter(item -> item.getVariant() != null && item.getVariant().getId().equals(variantId))
//                .findFirst();
//
//        if (existing.isPresent()) {
//            CartItem item = existing.get();
//            item.setQuantity(item.getQuantity() + quantity);
//        } else {
//            CartItem newItem = new CartItem();
//            Variation variant = new Variation();
//            variant.setId(variantId); // chỉ set ID tạm, JPA sẽ hiểu khi lưu
//            newItem.setVariant(variant);
//            newItem.setQuantity(quantity);
//            newItem.setCart(cart);
//            cart.getItems().add(newItem);
//        }
//
//        cartRepository.save(cart);
//    }

    public void updateQuantity(User user, Long itemId, String action) {
        Cart cart = getCartForUser(user);
        cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    if ("increase".equals(action)) {
                        item.setQuantity(item.getQuantity() + 1);
                    } else if ("decrease".equals(action) && item.getQuantity() > 1) {
                        item.setQuantity(item.getQuantity() - 1);
                    }
                });

        cartRepository.save(cart);
    }

    public void updateItemQuantity(Cart cart, Long itemId, String action) {
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow();

        if ("increase".equals(action)) {
            item.setQuantity(item.getQuantity() + 1);
        } else if ("decrease".equals(action) && item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        }

        cartItemRepository.save(item);
    }

    public int getQuantityForItem(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .map(CartItem::getQuantity)
                .findFirst()
                .orElse(0);
    }
//    public BigDecimal calculateTotal(Cart cart) {
//        if (cart == null || cart.getItems() == null) {
//            return BigDecimal.ZERO;
//        }
//
//        return cart.getItems().stream()
//                .map(item -> {
//                    // Lấy giá từ variant (product variant)
//                    Double salePrice = item.getVariant() != null ? item.getVariant().getSalePrice() : null;
//
//                    // Nếu salePrice là null thì sử dụng giá mặc định
//                    BigDecimal price = (salePrice != null)
//                            ? BigDecimal.valueOf(salePrice)
//                            : BigDecimal.ZERO;
//
//                    int quantity = item.getQuantity();
//
//                    return price.multiply(BigDecimal.valueOf(quantity));
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }


    public void removeUnchecked(User user, List<Long> uncheckedIds) {
        Cart cart = getCartForUser(user);
        cart.getItems().removeIf(item -> uncheckedIds.contains(item.getId()));
        cartRepository.save(cart);
    }
    public List<CheckoutItemView> buildCheckoutItems(List<CartItemRequest> items) {
        List<CheckoutItemView> result = new ArrayList<>();

        for (CartItemRequest item : items) {
            Long itemId = item.getItemId();
            int quantity = item.getQuantity();

            Optional<Product> productOpt = productRepository.findById(itemId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                double price = product.getSalePrice() != null ? product.getSalePrice() : product.getRegularPrice();
                result.add(new CheckoutItemView(itemId, product.getName(), price, quantity));
            }
        }

        return result;
    }



}
