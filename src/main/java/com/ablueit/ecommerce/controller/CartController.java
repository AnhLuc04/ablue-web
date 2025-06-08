//package com.ablueit.ecommerce.controller;
//
//import com.ablueit.ecommerce.model.Cart;
//import com.ablueit.ecommerce.model.DiscountCode;
//import com.ablueit.ecommerce.model.User;
//import com.ablueit.ecommerce.payload.request.CartItemRequest;
//import com.ablueit.ecommerce.payload.request.CheckoutItemView;
//import com.ablueit.ecommerce.repository.DiscountCodeRepository;
//import com.ablueit.ecommerce.repository.UserRepository;
//import com.ablueit.ecommerce.service.CartService;
//import jakarta.servlet.http.HttpSession;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.ModelAndView;
//
//import java.math.BigDecimal;
//import java.security.Principal;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//@Controller
//@RequestMapping("/cart")
//public class CartController {
//
//    @Autowired
//    private CartService cartService;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private DiscountCodeRepository discountCodeRepository;
//
//    @GetMapping
//    public ModelAndView showCart(Principal principal) {
//        ModelAndView modelAndView = new ModelAndView();
//
//        Optional<User> userOptional = userRepository.findByUsername(principal.getName());
//        if (userOptional.isEmpty()) {
//            modelAndView.setViewName("redirect:/login?error=userNotFound");
//            return modelAndView;
//        }
//
//        Cart cart = cartService.getCartForUser(userOptional.get());
//
//        modelAndView.setViewName("cart/view");
//        modelAndView.addObject("cart", cart);
//        modelAndView.addObject("totalPrice", cart.getTotal());
//
//        return modelAndView;
//    }
//
//    @PostMapping("/add")
//    public String addToCart(@RequestParam Long variantId, @RequestParam int quantity, Principal principal) {
//        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
//        cartService.addToCart(user, variantId, quantity); // sửa thành variantId
//        return "redirect:/cart";
//    }
//
//    @PostMapping("/update-ajax")
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> updateItemAjax(
//            @RequestParam("itemId") Long itemId,
//            @RequestParam("action") String action,
//            Principal principal) {
//
//        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
//        Cart cart = cartService.getCartForUser(user);
//
//        cartService.updateItemQuantity(cart, itemId, action);
//
//        BigDecimal totalPrice = cartService.calculateTotal(cart);
//        int updatedQty = cartService.getQuantityForItem(cart, itemId);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("quantity", updatedQty);
//        response.put("totalPrice", totalPrice);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/remove-ajax")
//    @ResponseBody
//    public ResponseEntity<?> removeUnchecked(@RequestBody List<Long> uncheckedIds, Principal principal) {
//        if (uncheckedIds == null || uncheckedIds.isEmpty()) {
//            return ResponseEntity.badRequest().body("Danh sách sản phẩm cần xóa đang trống.");
//        }
//
//        User user = userRepository.findByUsername(principal.getName())
//                .orElse(null);
//
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy người dùng.");
//        }
//
//        boolean removed = cartService.removeUnchecked(user, uncheckedIds);
//
//        if (removed) {
//            return ResponseEntity.ok("Đã xóa các sản phẩm chưa chọn khỏi giỏ hàng.");
//        } else {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Xảy ra lỗi khi xóa sản phẩm. Vui lòng thử lại sau.");
//        }
//    }
//
//
//    @GetMapping("/discounts")
//    @ResponseBody
//    public List<DiscountCode> getAllActiveDiscountsForUser() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String username = authentication.getName();
//        return discountCodeRepository.findByActiveTrueAndUserUsername(username);
//    }
//
//}
































package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.model.Cart;
import com.ablueit.ecommerce.model.DiscountCode;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.payload.request.CartItemRequest;
import com.ablueit.ecommerce.repository.DiscountCodeRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import com.ablueit.ecommerce.service.CartService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
        @Autowired
    private DiscountCodeRepository discountCodeRepository;
    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Helper method đọc cookie "cart" và parse ra Map<variantId, quantity>
    private Map<Long, Integer> getCartFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return new HashMap<>();
        for (Cookie cookie : cookies) {
            if ("cart".equals(cookie.getName())) {
                try {
                    String json = cookie.getValue();
                    if (json == null || json.isEmpty()) return new HashMap<>();
                    return objectMapper.readValue(json, new TypeReference<Map<Long, Integer>>() {});
                } catch (Exception e) {
                    e.printStackTrace();
                    return new HashMap<>();
                }
            }
        }
        return new HashMap<>();
    }

    // Helper method lưu giỏ hàng Map<variantId, quantity> thành cookie
    private void saveCartToCookie(HttpServletResponse response, Map<Long, Integer> cartMap) {
        try {
            String json = objectMapper.writeValueAsString(cartMap);
            Cookie cookie = new Cookie("cart", json);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 7); // 7 ngày
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Hiển thị giỏ hàng: nếu user đăng nhập -> lấy từ DB, nếu không -> lấy từ cookie
    @GetMapping
    public ModelAndView showCart(Principal principal, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();

        if (principal != null) {
            Optional<User> userOptional = userRepository.findByUsername(principal.getName());
            if (userOptional.isEmpty()) {
                modelAndView.setViewName("redirect:/login?error=userNotFound");
                return modelAndView;
            }
            Cart cart = cartService.getCartForUser(userOptional.get());
            modelAndView.addObject("cart", cart);
            modelAndView.addObject("totalPrice", cart.getTotal());
        } else {
            // User chưa đăng nhập -> đọc giỏ hàng từ cookie
            Map<Long, Integer> cartMap = getCartFromCookie(request);
            // Lấy dữ liệu chi tiết sản phẩm theo variantId để hiển thị (cần implement)
            Cart cart = cartService.buildCartFromMap(cartMap);
            modelAndView.addObject("cart", cart);
            modelAndView.addObject("totalPrice", cart.getTotal());
        }

        modelAndView.setViewName("cart/view");
        return modelAndView;
    }

    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public String addToCart(@RequestParam Long variantId,
                            @RequestParam int quantity,
                            Principal principal,
                            HttpServletRequest request,
                            HttpServletResponse response) {
        if (principal != null) {
            // User đăng nhập, lưu vào DB
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            cartService.addToCart(user, variantId, quantity);
        } else {
            // User chưa đăng nhập, lưu vào cookie
            Map<Long, Integer> cartMap = getCartFromCookie(request);
            cartMap.put(variantId, cartMap.getOrDefault(variantId, 0) + quantity);
            saveCartToCookie(response, cartMap);
        }
        return "redirect:/cart";
    }

    // Cập nhật số lượng item - AJAX
    @PostMapping("/update-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateItemAjax(@RequestParam("itemId") Long itemId,
                                                              @RequestParam("action") String action,
                                                              Principal principal,
                                                              HttpServletRequest request,
                                                              HttpServletResponse response) {
        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            Cart cart = cartService.getCartForUser(user);
            cartService.updateItemQuantity(cart, itemId, action);

            BigDecimal totalPrice = cartService.calculateTotal(cart);
            int updatedQty = cartService.getQuantityForItem(cart, itemId);

            Map<String, Object> res = new HashMap<>();
            res.put("quantity", updatedQty);
            res.put("totalPrice", totalPrice);
            return ResponseEntity.ok(res);
        } else {
            // User chưa đăng nhập, cập nhật cookie
            Map<Long, Integer> cartMap = getCartFromCookie(request);
            Integer currentQty = cartMap.getOrDefault(itemId, 0);
            if ("increase".equals(action)) {
                currentQty++;
            } else if ("decrease".equals(action)) {
                currentQty = Math.max(currentQty - 1, 0);
            }
            if (currentQty == 0) {
                cartMap.remove(itemId);
            } else {
                cartMap.put(itemId, currentQty);
            }
            saveCartToCookie(response, cartMap);

            Cart cart = cartService.buildCartFromMap(cartMap);
            Double totalPrice = cart.getTotal();

            Map<String, Object> res = new HashMap<>();
            res.put("quantity", currentQty);
            res.put("totalPrice", totalPrice);
            return ResponseEntity.ok(res);
        }
    }

    // Xóa các sản phẩm chưa chọn khỏi giỏ hàng (dùng ajax)
    @PostMapping("/remove-ajax")
    @ResponseBody
    public ResponseEntity<?> removeUnchecked(@RequestBody List<Long> uncheckedIds,
                                             Principal principal,
                                             HttpServletRequest request,
                                             HttpServletResponse response) {
        if (uncheckedIds == null || uncheckedIds.isEmpty()) {
            return ResponseEntity.badRequest().body("Danh sách sản phẩm cần xóa đang trống.");
        }

        if (principal != null) {
            User user = userRepository.findByUsername(principal.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy người dùng.");
            }
            boolean removed = cartService.removeUnchecked(user, uncheckedIds);
            if (removed) {
                return ResponseEntity.ok("Đã xóa các sản phẩm chưa chọn khỏi giỏ hàng.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Xảy ra lỗi khi xóa sản phẩm. Vui lòng thử lại sau.");
            }
        } else {
            // User chưa đăng nhập, xóa khỏi cookie
            Map<Long, Integer> cartMap = getCartFromCookie(request);
            for (Long id : uncheckedIds) {
                cartMap.remove(id);
            }
            saveCartToCookie(response, cartMap);
            return ResponseEntity.ok("Đã xóa các sản phẩm chưa chọn khỏi giỏ hàng.");
        }
    }
    @GetMapping("/discounts")
    @ResponseBody
    public List<DiscountCode> getAllActiveDiscountsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return discountCodeRepository.findByActiveTrueAndUserUsername(username);
    }
}
