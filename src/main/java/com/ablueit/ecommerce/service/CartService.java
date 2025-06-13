//package com.ablueit.ecommerce.service;
//
//import com.ablueit.ecommerce.model.*;
//import com.ablueit.ecommerce.payload.request.CartItemRequest;
//import com.ablueit.ecommerce.payload.request.CategoryRequest;
//import com.ablueit.ecommerce.payload.request.CheckoutItemView;
//import com.ablueit.ecommerce.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.*;
//
//@Service
//public class CartService {
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private CartItemRepository cartItemRepository;
//    @Autowired
//    ProductRepository productRepository;
//
////    @Autowired
////    VariantRepository variantRepository;
//
//    @Autowired
//    VariationRepository variationRepository;
//
//    public void addToCart(User user, Long variationId, int quantity) {
//        Cart cart = getCartForUser(user);
//
//        Optional<CartItem> existing = cart.getItems().stream()
//                .filter(item -> item.getVariation().getId().equals(variationId))
//                .findFirst();
//
//        if (existing.isPresent()) {
//            existing.get().setQuantity(existing.get().getQuantity() + quantity);
//        } else {
//            Variation variation = variationRepository.findById(variationId)
//                    .orElseThrow(() -> new RuntimeException("Variation not found"));
//
//            CartItem newItem = new CartItem();
//            newItem.setVariation(variation);
//            newItem.setQuantity(quantity);
//            newItem.setCart(cart);
//
//            cart.getItems().add(newItem);
//        }
//
//        cartRepository.save(cart);
//    }
//
//    public BigDecimal calculateTotal(Cart cart) {
//        if (cart == null || cart.getItems() == null) return BigDecimal.ZERO;
//
//        return cart.getItems().stream()
//                .map(item -> {
//                    double price = item.getVariation() != null ? item.getVariation().getPrice() : 0.0;
//                    return BigDecimal.valueOf(price).multiply(BigDecimal.valueOf(item.getQuantity()));
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//    public Cart getCartForUser(User user) {
//        return cartRepository.findByUser(user)
//                .orElseGet(() -> {
//                    Cart newCart = new Cart();
//                    newCart.setUser(user);
//                    return cartRepository.save(newCart);
//                });
//    }
//
//
////    public void addToCart(User user, Long variantId, int quantity) {
////        Cart cart = getCartForUser(user);
////
////        Optional<CartItem> existing = cart.getItems().stream()
////                .filter(item -> item.getVariant() != null && item.getVariant().getId().equals(variantId))
////                .findFirst();
////
////        if (existing.isPresent()) {
////            CartItem item = existing.get();
////            item.setQuantity(item.getQuantity() + quantity);
////        } else {
////            CartItem newItem = new CartItem();
////            Variation variant = new Variation();
////            variant.setId(variantId); // chỉ set ID tạm, JPA sẽ hiểu khi lưu
////            newItem.setVariant(variant);
////            newItem.setQuantity(quantity);
////            newItem.setCart(cart);
////            cart.getItems().add(newItem);
////        }
////
////        cartRepository.save(cart);
////    }
//
//    public void updateQuantity(User user, Long itemId, String action) {
//        Cart cart = getCartForUser(user);
//        cart.getItems().stream()
//                .filter(item -> item.getId().equals(itemId))
//                .findFirst()
//                .ifPresent(item -> {
//                    if ("increase".equals(action)) {
//                        item.setQuantity(item.getQuantity() + 1);
//                    } else if ("decrease".equals(action) && item.getQuantity() > 1) {
//                        item.setQuantity(item.getQuantity() - 1);
//                    }
//                });
//
//        cartRepository.save(cart);
//    }
//
//    public void updateItemQuantity(Cart cart, Long itemId, String action) {
//        CartItem item = cart.getItems().stream()
//                .filter(i -> i.getId().equals(itemId))
//                .findFirst()
//                .orElseThrow();
//
//        if ("increase".equals(action)) {
//            item.setQuantity(item.getQuantity() + 1);
//        } else if ("decrease".equals(action) && item.getQuantity() > 1) {
//            item.setQuantity(item.getQuantity() - 1);
//        }
//
//        cartItemRepository.save(item);
//    }
//
//    public int getQuantityForItem(Cart cart, Long itemId) {
//        return cart.getItems().stream()
//                .filter(i -> i.getId().equals(itemId))
//                .map(CartItem::getQuantity)
//                .findFirst()
//                .orElse(0);
//    }
////    public BigDecimal calculateTotal(Cart cart) {
////        if (cart == null || cart.getItems() == null) {
////            return BigDecimal.ZERO;
////        }
////
////        return cart.getItems().stream()
////                .map(item -> {
////                    // Lấy giá từ variant (product variant)
////                    Double salePrice = item.getVariant() != null ? item.getVariant().getSalePrice() : null;
////
////                    // Nếu salePrice là null thì sử dụng giá mặc định
////                    BigDecimal price = (salePrice != null)
////                            ? BigDecimal.valueOf(salePrice)
////                            : BigDecimal.ZERO;
////
////                    int quantity = item.getQuantity();
////
////                    return price.multiply(BigDecimal.valueOf(quantity));
////                })
////                .reduce(BigDecimal.ZERO, BigDecimal::add);
////    }
//
//    public boolean removeUnchecked(User user, List<Long> uncheckedIds) {
//        try {
//            Cart cart = cartRepository.findByUser(user).get();
//            if (cart == null) return false;
//
//            cart.getItems().removeIf(item -> uncheckedIds.contains(item.getId()));
//
//            cartRepository.save(cart); // Lưu lại giỏ hàng sau khi xóa
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
////    public void removeUnchecked(User user, List<Long> uncheckedIds) {
////        Cart cart = getCartForUser(user);
////        cart.getItems().removeIf(item -> uncheckedIds.contains(item.getId()));
////        cartRepository.save(cart);
////    }
//    public List<CheckoutItemView> buildCheckoutItems(List<CartItemRequest> items) {
//        List<CheckoutItemView> result = new ArrayList<>();
//
//        for (CartItemRequest item : items) {
//            Long itemId = item.getItemId();
//            int quantity = item.getQuantity();
//
//            Optional<Product> productOpt = productRepository.findById(itemId);
//            if (productOpt.isPresent()) {
//                Product product = productOpt.get();
//                double price = product.getSalePrice() != null ? product.getSalePrice() : product.getRegularPrice();
//                result.add(new CheckoutItemView(itemId, product.getName(), price, quantity));
//            }
//        }
//
//        return result;
//    }
//
//
//
//}






































package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.*;
import com.ablueit.ecommerce.payload.request.CheckoutItemView;
import com.ablueit.ecommerce.repository.CartItemRepository;
import com.ablueit.ecommerce.repository.CartRepository;

import com.ablueit.ecommerce.repository.ProductVariationRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import jakarta.transaction.Transactional;
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
    private ProductVariationRepository productVariantRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Lấy giỏ hàng theo user.
     * Nếu chưa có, tạo mới.
     */
    public Cart getCartForUser(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> {
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setItems(new ArrayList<>());
            return cartRepository.save(cart);
        });
    }

    /**
     * Thêm sản phẩm vào giỏ hàng của user
     */
    @Transactional
    public void addToCart(User user, Long variantId, int quantity) {
        Cart cart = getCartForUser(user);

        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(item -> item.getVariation().getId().equals(variantId))
                .findFirst();

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
        } else {
            Variation variant = productVariantRepository.findById(variantId)
                    .orElseThrow(() -> new RuntimeException("Variant not found"));
            CartItem newItem = new CartItem();
            newItem.setVariation(variant);
            newItem.setQuantity(quantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cartRepository.save(cart);
    }

    /**
     * Cập nhật số lượng sản phẩm trong giỏ hàng theo action "increase" hoặc "decrease"
     */
    @Transactional
    public void updateItemQuantity(Cart cart, Long itemId, String action) {
        Optional<CartItem> optionalCartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst();

        if (optionalCartItem.isPresent()) {
            CartItem item = optionalCartItem.get();
            int qty = item.getQuantity();
            if ("increase".equals(action)) {
                qty++;
            } else if ("decrease".equals(action)) {
                qty = Math.max(qty - 1, 0);
            }
            if (qty == 0) {
                cart.getItems().remove(item);
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(qty);
                cartItemRepository.save(item);
            }
            cartRepository.save(cart);
        }
    }

    public BigDecimal calculateTotal(Cart cart) {
        return cart.getItems().stream()
                .map(item -> {
                    Double price = item.getVariation().getPrice(); // giả sử trả về Double
                    BigDecimal priceBD = BigDecimal.valueOf(price); // chuyển Double -> BigDecimal
                    return priceBD.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    public int getQuantityForItem(Cart cart, Long itemId) {
        return cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .map(CartItem::getQuantity)
                .orElse(0);
    }

    /**
     * Xóa các sản phẩm chưa chọn khỏi giỏ hàng user
     */
    @Transactional
    public boolean removeUnchecked(User user, List<Long> uncheckedItemIds) {
        try {
            Cart cart = getCartForUser(user);
            List<CartItem> itemsToRemove = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                if (uncheckedItemIds.contains(item.getId())) {
                    itemsToRemove.add(item);
                }
            }
            cart.getItems().removeAll(itemsToRemove);
            cartItemRepository.deleteAll(itemsToRemove);
            cartRepository.save(cart);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * PHẦN CHO USER CHƯA ĐĂNG NHẬP: Tạo Cart từ Map<variantId, quantity> (cookie)
     * Lấy dữ liệu chi tiết product variant từ DB rồi xây dựng Cart object ảo
     */
    public Cart buildCartFromMap(Map<Long, Integer> cartMap) {
        Cart cart = new Cart();
        List<CartItem> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            Long variantId = entry.getKey();
            Integer qty = entry.getValue();

            productVariantRepository.findById(variantId).ifPresent(variant -> {
                CartItem item = new CartItem();
                item.setVariation(variant);
                item.setQuantity(qty);
                items.add(item);
            });
        }

        cart.setItems(items);
        return cart;
    }
    public void removeFromCart(User user, Long variantId) {
        Cart cart = getCartForUser(user);
        cart.removeItemByVariantId(variantId);
        cartRepository.save(cart);
    }

}