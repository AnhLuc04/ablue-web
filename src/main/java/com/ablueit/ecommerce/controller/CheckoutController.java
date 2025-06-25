package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.model.*;
import com.ablueit.ecommerce.repository.CartRepository;
import com.ablueit.ecommerce.repository.ProductVariationRepository;
import com.ablueit.ecommerce.repository.UserRepository;
;
import com.ablueit.ecommerce.service.CartService;

//import com.ablueit.ecommerce.service.OrderService;
import com.ablueit.ecommerce.service.impl.AddressService;
import com.ablueit.ecommerce.service.impl.CouponService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final CouponService couponService;
    private final AddressService addressService;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductVariationRepository productVariationRepository;
//    private final OrderService orderService;

    /**
     * Trường hợp người dùng vào trực tiếp trang checkout (không chọn checkbox)
     */
    @GetMapping
    public String showCheckout(Model model,
                               HttpSession session,
                               HttpServletRequest request,
                               Principal principal) {

        List<CartItem> cartItems = new ArrayList<>();
        User user = null;

        if (principal != null) {
            user = userRepository.findByUsername(principal.getName()).orElse(null);
        }

        if (user != null) {
            // Đã đăng nhập
            List<Long> selectedItemIds = getSelectedItemIdsFromCookie(request);
            if (!selectedItemIds.isEmpty()) {
                cartItems = cartService.getCartItemsByIds(selectedItemIds, user);
            } else {
                Optional<Cart> cart = cartRepository.findByUser(user);
                cartItems = cart.map(Cart::getItems).orElse(List.of());
            }
        } else {
            // Chưa đăng nhập => lấy từ cookie "cart" dạng variantId:quantity|...
            Map<Long, Integer> guestCartMap = getCartFromCookie(request);
            if (!guestCartMap.isEmpty()) {
                cartItems = buildGuestCartItems(guestCartMap); // 🧠 Tạo cartItems tạm thời
            }
        }

        if (cartItems == null || cartItems.isEmpty()) {
            model.addAttribute("cartItems", List.of());
            model.addAttribute("subtotal", 0);
            model.addAttribute("shippingFee", 0);
            model.addAttribute("discount", 0);
            model.addAttribute("total", 0);
            return "checkout/view";
        }

        double subtotal = cartService.calculateSubtotal(cartItems);
        double shippingFee = cartService.getShippingFee();
        double discount = 0.0;

        CodeCoupon selectedCoupon = (CodeCoupon) session.getAttribute("selectedCoupon");
        if (selectedCoupon != null && selectedCoupon.isValid(LocalDateTime.now())) {
            discount = couponService.calculateDiscount(selectedCoupon, subtotal);
        }

        double total = subtotal + shippingFee - discount;
        Double totalFromCookie = getCartTotalFromCookie(request);
        if (totalFromCookie != null && totalFromCookie > 0) {
            total = totalFromCookie;
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("discount", discount);
        model.addAttribute("total", total);
        model.addAttribute("selectedCoupon", selectedCoupon);

        if (user != null) {
            model.addAttribute("userCoupons", couponService.findUserCoupons(user));
            model.addAttribute("userAddresses", addressService.getAddressesByUser(user));
        }

        return "checkout/view";
    }





    private Map<Long, Integer> getCartFromCookie(HttpServletRequest request) {
        Map<Long, Integer> cartMap = new HashMap<>();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("cart".equals(cookie.getName())) {
                    String[] items = cookie.getValue().split("\\|");
                    for (String item : items) {
                        String[] parts = item.split(":");
                        if (parts.length == 2) {
                            try {
                                Long variantId = Long.parseLong(parts[0]);
                                Integer quantity = Integer.parseInt(parts[1]);
                                cartMap.put(variantId, quantity);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        }
        return cartMap;
    }






    public List<CartItem> buildGuestCartItems(Map<Long, Integer> guestCartMap) {
        List<CartItem> guestItems = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : guestCartMap.entrySet()) {
            Long variantId = entry.getKey();
            Integer quantity = entry.getValue();

            Variation variation = productVariationRepository.findById(variantId).orElse(null);
            if (variation != null) {
                CartItem item = new CartItem();
                item.setVariation(variation);
                item.setQuantity(quantity);
                guestItems.add(item);
            }
        }
        return guestItems;
    }





//        @GetMapping()
//        public String showCheckout(Model model,
//                                   HttpSession session,
//                                   HttpServletRequest request,
//                                   Principal principal) {
//
//            List<CartItem> cartItems = new ArrayList<>();
//            List<Long> selectedItemIds = (List<Long>) session.getAttribute("selectedItemIds");
//
//            User user = null;
//            if (principal != null) {
//                String username = principal.getName();
//                user = userRepository.findByUsername(username).orElse(null);
//            }
//
//            if (selectedItemIds != null && !selectedItemIds.isEmpty()) {
//                if (user != null) {
//                    cartItems = cartService.getCartItemsByIds(selectedItemIds, user);
//                } else {
//                    cartItems = cartService.getGuestCartItemsFromIds(selectedItemIds);
//                }
//                session.removeAttribute("selectedItemIds");
//            } else {
//                List<Long> cookieItemIds = getSelectedItemIdsFromCookie(request);
//                if (!cookieItemIds.isEmpty()) {
//                    if (user != null) {
//                        cartItems = cartService.getCartItemsByIds(cookieItemIds, user);
//                    } else {
//                        cartItems = cartService.getGuestCartItemsFromIds(cookieItemIds);
//                    }
//                } else if (user != null) {
//                    Optional<Cart> cart = cartRepository.findByUser(user);
//                    cartItems = cart.map(Cart::getItems).orElse(List.of());
//                }
//            }
//
//            if (cartItems == null || cartItems.isEmpty()) {
//                model.addAttribute("cartItems", List.of());
//                model.addAttribute("subtotal", 0);
//                model.addAttribute("shippingFee", 0);
//                model.addAttribute("discount", 0);
//                model.addAttribute("total", 0);
//                return "checkout/view";
//            }
//
//            // Tính giá
//            double subtotal = cartService.calculateSubtotal(cartItems);
//            double shippingFee = cartService.getShippingFee();
//            double discount = 0.0;
//
//            CodeCoupon selectedCoupon = (CodeCoupon) session.getAttribute("selectedCoupon");
//            if (selectedCoupon != null && selectedCoupon.isValid(LocalDateTime.now())) {
//                discount = couponService.calculateDiscount(selectedCoupon, subtotal);
//            }
//
//            double total = subtotal + shippingFee - discount;
//
//            // Ưu tiên lấy từ cookie nếu có
//            Double totalFromCookie = getCartTotalFromCookie(request);
//            if (totalFromCookie != null && totalFromCookie > 0) {
//                total = totalFromCookie;
//            }
//
//            model.addAttribute("cartItems", cartItems);
//            model.addAttribute("subtotal", subtotal);
//            model.addAttribute("shippingFee", shippingFee);
//            model.addAttribute("discount", discount);
//            model.addAttribute("total", total);
//            model.addAttribute("selectedCoupon", selectedCoupon);
//
//            if (user != null) {
//                List<CodeCoupon> userCoupons = couponService.findUserCoupons(user);
//                List<Address> userAddresses = addressService.getAddressesByUser(user);
//                model.addAttribute("userCoupons", userCoupons);
//                model.addAttribute("userAddresses", userAddresses);
//            }
//
//            return "checkout/view";
//        }
//




//
//    private Double getCartTotalFromCookie(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("selectedCartTotal".equals(cookie.getName())) {
//                    try {
//                        return Double.parseDouble(cookie.getValue());
//                    } catch (NumberFormatException ignored) {
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//





    private List<Long> getSelectedItemIdsFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("selectedItemIds".equals(cookie.getName())) {
                    String[] parts = cookie.getValue().split(",");
                    List<Long> ids = new ArrayList<>();
                    for (String part : parts) {
                        try {
                            ids.add(Long.parseLong(part));
                        } catch (NumberFormatException ignored) {}
                    }
                    return ids;
                }
            }
        }
        return new ArrayList<>();
    }

    private Double getCartTotalFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("selectedCartTotal".equals(cookie.getName())) {
                    try {
                        return Double.parseDouble(cookie.getValue());
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return null;
    }

//    @PostMapping("/place")
//    public String placeOrder(@ModelAttribute OrderFormDTO form, Principal principal, RedirectAttributes redirectAttributes) {
//        try {
//            orderService.createOrder(form, principal.getName());
//            redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
//            return "redirect:/order/confirmation";
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Failed to place order. Try again.");
//            return "redirect:/checkout";
//        }
//    }
//
//    @GetMapping("/confirmation")
//    public String showConfirmation() {
//        return "order/confirmation"; // Tạo một trang confirmation đơn giản
//    }
}
