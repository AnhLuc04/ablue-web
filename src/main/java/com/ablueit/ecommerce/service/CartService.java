
package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.*;
import com.ablueit.ecommerce.payload.request.CheckoutItemView;
import com.ablueit.ecommerce.repository.CartItemRepository;
import com.ablueit.ecommerce.repository.CartRepository;

import com.ablueit.ecommerce.repository.ProductVariationRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
//
//@Service
//public class CartService {
//
//    @Autowired
//    private CartRepository cartRepository;
//
//    @Autowired
//    private CartItemRepository cartItemRepository;
//
//    @Autowired
//    private ProductVariationRepository productVariantRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    /**
//     * Lấy giỏ hàng theo user.
//     * Nếu chưa có, tạo mới.
//     */
//    public Cart getCartForUser(User user) {
//        return cartRepository.findByUser(user).orElseGet(() -> {
//            Cart cart = new Cart();
//            cart.setUser(user);
//            cart.setItems(new ArrayList<>());
//            return cartRepository.save(cart);
//        });
//    }
//
//    /**
//     * Thêm sản phẩm vào giỏ hàng của user
//     */
//    @Transactional
//    public void addToCart(User user, Long variantId, int quantity) {
//        Cart cart = getCartForUser(user);
//
//        Optional<CartItem> optionalCartItem = cart.getItems().stream()
//                .filter(item -> item.getVariation().getId().equals(variantId))
//                .findFirst();
//
//        if (optionalCartItem.isPresent()) {
//            CartItem cartItem = optionalCartItem.get();
//            cartItem.setQuantity(cartItem.getQuantity() + quantity);
//            cartItemRepository.save(cartItem);
//        } else {
//            Variation variant = productVariantRepository.findById(variantId)
//                    .orElseThrow(() -> new RuntimeException("Variant not found"));
//            CartItem newItem = new CartItem();
//            newItem.setVariation(variant);
//            newItem.setQuantity(quantity);
//            newItem.setCart(cart);
//            cart.getItems().add(newItem);
//            cartItemRepository.save(newItem);
//        }
//
//        cartRepository.save(cart);
//    }
//
//    /**
//     * Cập nhật số lượng sản phẩm trong giỏ hàng theo action "increase" hoặc "decrease"
//     */
//    @Transactional
//    public void updateItemQuantity(Cart cart, Long itemId, String action) {
//        Optional<CartItem> optionalCartItem = cart.getItems().stream()
//                .filter(item -> item.getId().equals(itemId))
//                .findFirst();
//
//        if (optionalCartItem.isPresent()) {
//            CartItem item = optionalCartItem.get();
//            int qty = item.getQuantity();
//            if ("increase".equals(action)) {
//                qty++;
//            } else if ("decrease".equals(action)) {
//                qty = Math.max(qty - 1, 0);
//            }
//            if (qty == 0) {
//                cart.getItems().remove(item);
//                cartItemRepository.delete(item);
//            } else {
//                item.setQuantity(qty);
//                cartItemRepository.save(item);
//            }
//            cartRepository.save(cart);
//        }
//    }
//
//    public BigDecimal calculateTotal(Cart cart) {
//        return cart.getItems().stream()
//                .map(item -> {
//                    Double price = item.getVariation().getPrice(); // giả sử trả về Double
//                    BigDecimal priceBD = BigDecimal.valueOf(price); // chuyển Double -> BigDecimal
//                    return priceBD.multiply(BigDecimal.valueOf(item.getQuantity()));
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//
//
//    public int getQuantityForItem(Cart cart, Long itemId) {
//        return cart.getItems().stream()
//                .filter(i -> i.getId().equals(itemId))
//                .findFirst()
//                .map(CartItem::getQuantity)
//                .orElse(0);
//    }
//
//    /**
//     * Xóa các sản phẩm chưa chọn khỏi giỏ hàng user
//     */
//    @Transactional
//    public boolean removeUnchecked(User user, List<Long> uncheckedItemIds) {
//        try {
//            Cart cart = getCartForUser(user);
//            List<CartItem> itemsToRemove = new ArrayList<>();
//            for (CartItem item : cart.getItems()) {
//                if (uncheckedItemIds.contains(item.getId())) {
//                    itemsToRemove.add(item);
//                }
//            }
//            cart.getItems().removeAll(itemsToRemove);
//            cartItemRepository.deleteAll(itemsToRemove);
//            cartRepository.save(cart);
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    /**
//     * PHẦN CHO USER CHƯA ĐĂNG NHẬP: Tạo Cart từ Map<variantId, quantity> (cookie)
//     * Lấy dữ liệu chi tiết product variant từ DB rồi xây dựng Cart object ảo
//     */
//    public Cart buildCartFromMap(Map<Long, Integer> cartMap) {
//        Cart cart = new Cart();
//        List<CartItem> items = new ArrayList<>();
//
//        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
//            Long variantId = entry.getKey();
//            Integer qty = entry.getValue();
//
//            productVariantRepository.findById(variantId).ifPresent(variant -> {
//                CartItem item = new CartItem();
//                item.setVariation(variant);
//                item.setQuantity(qty);
//                items.add(item);
//            });
//        }
//
//        cart.setItems(items);
//        return cart;
//    }
//    public void removeFromCart(User user, Long variantId) {
//        Cart cart = getCartForUser(user);
//        cart.removeItemByVariantId(variantId);
//        cartRepository.save(cart);
//    }
//
//}






@Service
public class CartService {

    @Autowired
    private  CartRepository cartRepository;
    @Autowired
    private  CartItemRepository cartItemRepository;
    @Autowired
    private  ProductVariationRepository productVariantRepository;

    public List<CartItem> getGuestCartItemsFromIds(List<Long> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) return List.of();
        return cartItemRepository.findAllById(itemIds);
    }
    public List<CartItem> getGuestCartItemsFromMap(List<Long> selectedIds, Map<Long, Integer> cartMap) {
        List<CartItem> items = new ArrayList<>();
        for (Long variantId : selectedIds) {
            if (cartMap.containsKey(variantId)) {
                Variation variant = productVariantRepository.findById(variantId).orElse(null);
                if (variant != null) {
                    CartItem item = new CartItem();
                    item.setVariation(variant);
                    item.setQuantity(cartMap.get(variantId));
                    items.add(item);
                }
            }
        }
        return items;
    }

    /**
     * ➤ Thêm sản phẩm vào cart của user + store
     */
    public void addToCart(User user, Long variantId, int quantity, Store store) {
        Variation variant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // ➤ Tìm cart của user theo store
        Cart cart = cartRepository.findByUserAndStore(user, store)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setStore(store);
                    return cartRepository.save(newCart);
                });

        // ➤ Kiểm tra item đã có chưa
        Optional<CartItem> optionalItem = cart.getItems().stream()
                .filter(i -> i.getVariation().getId().equals(variantId))
                .findFirst();

        if (optionalItem.isPresent()) {
            CartItem item = optionalItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem();
            item.setVariation(variant);
            item.setQuantity(quantity);
            item.setCart(cart);
            cart.getItems().add(item);
        }

        cartRepository.save(cart);
    }

    /**
     * ➤ Lấy danh sách item trong giỏ hàng (theo user + store)
     */
    public List<CartItem> getCartItems(User user, Store store) {
        return cartRepository.findByUserAndStore(user, store)
                .map(Cart::getItems)
                .orElse(Collections.emptyList());
    }

    /**
     * ➤ Xử lý giỏ hàng từ cookie (người dùng chưa login)
     */
    public List<CartItem> getCartItemsFromCookie(Map<Long, Integer> cartMap) {
        List<CartItem> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            Long variantId = entry.getKey();
            int quantity = entry.getValue();

            productVariantRepository.findById(variantId).ifPresent(variant -> {
                CartItem item = new CartItem();
                item.setId(variantId); // Gán ID để hiển thị (hoặc ID tạm nếu cần)
                item.setVariation(variant); // Gán sản phẩm chi tiết
                item.setQuantity(quantity);
                item.setCart(null); // Không gán cart khi chưa login
                items.add(item);
            });
        }

        return items;
    }


    /**
     * ➤ Cập nhật số lượng trong cart
     */
    public void updateQuantity(User user, Long variantId, int quantity, Store store) {
        Cart cart = cartRepository.findByUserAndStore(user, store)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getVariation().getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }
    public Cart getOrCreateCart(User user, Store store) {
        return cartRepository.findByUserAndStore(user, store)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setStore(store);  // Quan trọng
                    return cartRepository.save(cart);
                });
    }
    public void updateItemQuantity(Long itemId, int quantity, User user) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getCart().getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);
    }
    public void removeFromCart(User user, Long variationId) {
        Cart cart = cartRepository.findByUser(user).orElseThrow();
        cart.removeItemByVariantId(variationId);
        cartRepository.save(cart);
    }
    public List<CartItem> getCartItemsByIds(List<Long> itemIds, User user) {
        return cartItemRepository.findByIdInAndCartUser(itemIds, user);
    }

    /**
     * ➤ Xóa item khỏi giỏ
     */
    public void removeItem(User user, Long variantId, Store store) {
        Cart cart = cartRepository.findByUserAndStore(user, store)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giỏ hàng"));

        cart.getItems().removeIf(i -> i.getVariation().getId().equals(variantId));
        cartRepository.save(cart);
    }







    public List<CartItem> getCurrentUserCartItems(User user, HttpSession session) {
        if (user != null) {
            return cartItemRepository.findByUser(user);
        } else {
            // Lấy từ session nếu chưa đăng nhập
            List<CartItem> guestCart = (List<CartItem>) session.getAttribute("guestCart");
            return guestCart != null ? guestCart : new ArrayList<>();
        }
    }

    public double calculateSubtotal(List<CartItem> items) {
        return items.stream()
                .mapToDouble(item -> {
                    if (item == null || item.getVariation() == null) return 0.0;

                    double price = item.getVariation().getSalePrice() != null ? item.getVariation().getSalePrice() : 0.0;
                    int quantity = item.getQuantity(); // KHÔNG kiểm tra null ở đây nếu là int

                    return quantity * price;
                })
                .sum();
    }




    public double getShippingFee() {
        return 30.0; // cố định 30,000đ hoặc có thể linh hoạt theo địa chỉ giao hàng
    }


}
