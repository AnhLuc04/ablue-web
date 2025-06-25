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

import com.ablueit.ecommerce.model.*;
import com.ablueit.ecommerce.payload.request.CartItemRequest;
//import com.ablueit.ecommerce.repository.DiscountCodeRepository;
import com.ablueit.ecommerce.repository.StoreRepository;
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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

//@Controller
//@RequestMapping("/cart")
//public class CartController {
//
//    @Autowired
//    private CartService cartService;
//    @Autowired
//    private DiscountCodeRepository discountCodeRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    private Map<Long, Integer> getCartFromCookie(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if ("cart".equals(cookie.getName())) {
//                    try {
//                        String decodedJson = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8.toString());
//                        return objectMapper.readValue(decodedJson, new TypeReference<Map<Long, Integer>>() {});
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return new HashMap<>();
//    }
//
//
//    private void saveCartToCookie(HttpServletResponse response, Map<Long, Integer> cartMap) {
//        try {
//            String json = objectMapper.writeValueAsString(cartMap);
//            String encodedJson = URLEncoder.encode(json, StandardCharsets.UTF_8.toString());
//            Cookie cookie = new Cookie("cart", encodedJson);
//            cookie.setPath("/");
//            cookie.setMaxAge(60 * 60 * 24 * 7); // 7 ngày
//            response.addCookie(cookie);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    // Hiển thị giỏ hàng: nếu user đăng nhập -> lấy từ DB, nếu không -> lấy từ cookie
//    @GetMapping
//    public ModelAndView showCart(Principal principal, HttpServletRequest request) {
//        ModelAndView modelAndView = new ModelAndView();
//
//        if (principal != null) {
//            Optional<User> userOptional = userRepository.findByUsername(principal.getName());
//            if (userOptional.isEmpty()) {
//                modelAndView.setViewName("redirect:/login?error=userNotFound");
//                return modelAndView;
//            }
//            Cart cart = cartService.getCartForUser(userOptional.get());
//            modelAndView.addObject("cart", cart);
//            modelAndView.addObject("totalPrice", cart.getTotal());
//        } else {
//            // User chưa đăng nhập -> đọc giỏ hàng từ cookie
//            Map<Long, Integer> cartMap = getCartFromCookie(request);
//            // Lấy dữ liệu chi tiết sản phẩm theo variantId để hiển thị (cần implement)
//            Cart cart = cartService.buildCartFromMap(cartMap);
//            modelAndView.addObject("cart", cart);
//            modelAndView.addObject("totalPrice", cart.getTotal());
//        }
//
//        modelAndView.setViewName("cart/view");
//        return modelAndView;
//    }
//
//    @PostMapping("/add")
//    public String addToCart(@RequestParam(name = "variantId") Long variantId,
//                            @RequestParam(name = "quantity") int quantity,
//                            Principal principal,
//                            HttpServletRequest request,
//                            HttpServletResponse response) {
//        if (principal != null) {
//            // User đăng nhập, lưu vào DB
//            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
//            cartService.addToCart(user, variantId, quantity);
//        } else {
//            // User chưa đăng nhập, lưu vào cookie
//            Map<Long, Integer> cartMap = getCartFromCookie(request);
//            cartMap.put(variantId, cartMap.getOrDefault(variantId, 0) + quantity);
//            saveCartToCookie(response, cartMap);
//        }
//        return "redirect:/cart";
//    }
//
//    // Cập nhật số lượng item - AJAX
//    @PostMapping("/update-ajax")
//    @ResponseBody
//    public ResponseEntity<Map<String, Object>> updateItemAjax(@RequestParam("itemId") Long itemId,
//                                                              @RequestParam("action") String action,
//                                                              Principal principal,
//                                                              HttpServletRequest request,
//                                                              HttpServletResponse response) {
//        if (principal != null) {
//            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
//            Cart cart = cartService.getCartForUser(user);
//            cartService.updateItemQuantity(cart, itemId, action);
//
//            BigDecimal totalPrice = cartService.calculateTotal(cart);
//            int updatedQty = cartService.getQuantityForItem(cart, itemId);
//
//            Map<String, Object> res = new HashMap<>();
//            res.put("quantity", updatedQty);
//            res.put("totalPrice", totalPrice);
//            return ResponseEntity.ok(res);
//        } else {
//            // User chưa đăng nhập, cập nhật cookie
//            Map<Long, Integer> cartMap = getCartFromCookie(request);
//            Integer currentQty = cartMap.getOrDefault(itemId, 0);
//            if ("increase".equals(action)) {
//                currentQty++;
//            } else if ("decrease".equals(action)) {
//                currentQty = Math.max(currentQty - 1, 0);
//            }
//            if (currentQty == 0) {
//                cartMap.remove(itemId);
//            } else {
//                cartMap.put(itemId, currentQty);
//            }
//            saveCartToCookie(response, cartMap);
//
//            Cart cart = cartService.buildCartFromMap(cartMap);
//            Double totalPrice = cart.getTotal();
//
//            Map<String, Object> res = new HashMap<>();
//            res.put("quantity", currentQty);
//            res.put("totalPrice", totalPrice);
//            return ResponseEntity.ok(res);
//        }
//    }
//
//    // Xóa các sản phẩm chưa chọn khỏi giỏ hàng (dùng ajax)
//    @PostMapping("/remove-ajax")
//    @ResponseBody
//    public ResponseEntity<?> removeUnchecked(@RequestBody List<Long> uncheckedIds,
//                                             Principal principal,
//                                             HttpServletRequest request,
//                                             HttpServletResponse response) {
//        if (uncheckedIds == null || uncheckedIds.isEmpty()) {
//            return ResponseEntity.badRequest().body("Danh sách sản phẩm cần xóa đang trống.");
//        }
//
//        if (principal != null) {
//            User user = userRepository.findByUsername(principal.getName()).orElse(null);
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Không tìm thấy người dùng.");
//            }
//            boolean removed = cartService.removeUnchecked(user, uncheckedIds);
//            if (removed) {
//                return ResponseEntity.ok("Đã xóa các sản phẩm chưa chọn khỏi giỏ hàng.");
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                        .body("Xảy ra lỗi khi xóa sản phẩm. Vui lòng thử lại sau.");
//            }
//        } else {
//            // User chưa đăng nhập, xóa khỏi cookie
//            Map<Long, Integer> cartMap = getCartFromCookie(request);
//            for (Long id : uncheckedIds) {
//                cartMap.remove(id);
//            }
//            saveCartToCookie(response, cartMap);
//            return ResponseEntity.ok("Đã xóa các sản phẩm chưa chọn khỏi giỏ hàng.");
//        }
//    }
//    @PostMapping("/remove")
//    @ResponseBody
//    public Map<String, Object> removeFromCart(@RequestParam Long itemId, Principal principal, HttpServletRequest request, HttpServletResponse response) {
//        Map<String, Object> result = new HashMap<>();
//        try {
//            if (principal != null) {
//                User user = userRepository.findByUsername(principal.getName()).orElseThrow();
//                cartService.removeFromCart(user, itemId);
//            } else {
//                Map<Long, Integer> cartMap = getCartFromCookie(request);
//                cartMap.remove(itemId);
//                saveCartToCookie(response, cartMap);
//            }
//            result.put("success", true);
//        } catch (Exception e) {
//            result.put("success", false);
//        }
//        return result;
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
//}


























@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public CartController(CartService cartService,
                          StoreRepository storeRepository,
                          UserRepository userRepository) {
        this.cartService = cartService;
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test/add-to-cart-test";
    }

    @PostMapping("/add")
            public String addToCart(@RequestParam(name = "variantId") Long variantId,
                            @RequestParam(name = "quantity") int quantity,
                            HttpServletRequest request,
                            HttpServletResponse response,
                            Principal principal) {

        // ➤ Xác định Store theo domain
        String domain = request.getServerName(); // ví dụ: store1.yoursite.com
        Optional<Store> optionalStore = storeRepository.findByDomain(domain);
        if (optionalStore.isEmpty()) {
            return "redirect:/error"; // hoặc trả về thông báo lỗi tùy bạn xử lý
        }

        Store store = optionalStore.get();

        if (principal != null) {
            // ➤ Người dùng đã đăng nhập
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();
            cartService.addToCart(user, variantId, quantity, store);
        } else {
            // ➤ Người dùng chưa đăng nhập – xử lý bằng cookie
            Map<Long, Integer> cartMap = getCartFromCookie(request);
            cartMap.put(variantId, cartMap.getOrDefault(variantId, 0) + quantity);
            saveCartToCookie(response, cartMap);
            // Nếu muốn phân biệt store, có thể lưu storeId vào cookie
        }

        return "redirect:/cart";
    }
    @GetMapping
    public String viewCart(HttpServletRequest request,
                           Principal principal,
                           Model model) {
        // ➤ Xác định Store theo domain
        String domain = request.getServerName();
        Optional<Store> optionalStore = storeRepository.findByDomain(domain);
        if (optionalStore.isEmpty()) {
            return "redirect:/error";
        }
        Store store = optionalStore.get();

        List<CartItem> cartItems;
        double total = 0.0;

        if (principal != null) {
            // ➤ Người dùng đã đăng nhập
            User user = userRepository.findByUsername(principal.getName()).orElseThrow();

            Cart cart = cartService.getOrCreateCart(user, store);  // luôn trả về Cart hợp lệ
            cartItems = cart.getItems(); // đảm bảo đã fetch đầy đủ item
            total = cart.getTotal();

        } else {
            // ➤ Người dùng chưa đăng nhập – dùng cookie
            Map<Long, Integer> cartMap = getCartFromCookie(request);
            cartItems = cartService.getCartItemsFromCookie(cartMap);
            total = cartItems.stream()
                    .mapToDouble(CartItem::getSubtotal)
                    .sum();
        }

        // ➤ Truyền dữ liệu ra view
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        return "cart/view";
    }

    // ---------- Helper Methods (cookie support) ----------

    private Map<Long, Integer> getCartFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        Map<Long, Integer> cartMap = new HashMap<>();
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
                            } catch (NumberFormatException e) {
                                // ignore invalid cookie format
                            }
                        }
                    }
                }
            }
        }
        return cartMap;
    }

    private void saveCartToCookie(HttpServletResponse response, Map<Long, Integer> cartMap) {
        StringBuilder cookieValue = new StringBuilder();
        for (Map.Entry<Long, Integer> entry : cartMap.entrySet()) {
            if (cookieValue.length() > 0) {
                cookieValue.append("|");
            }
            cookieValue.append(entry.getKey()).append(":").append(entry.getValue());
        }

        Cookie cookie = new Cookie("cart", cookieValue.toString());
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        response.addCookie(cookie);
    }






    @PostMapping("/update-quantity")
    @ResponseBody
    public ResponseEntity<?> updateQuantity(@RequestParam("itemId") Long itemId,
                                            @RequestParam("quantity")  int quantity,
                                            Principal principal,
                                            HttpServletRequest request,
                                            HttpServletResponse response) {
        if (quantity <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be greater than 0");
        }

        if (principal != null) {
            // Người dùng đã đăng nhập → cập nhật DB
            Optional<User> optionalUser = userRepository.findByUsername(principal.getName());
            if (optionalUser.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

            cartService.updateItemQuantity(itemId, quantity, optionalUser.get());
            return ResponseEntity.ok().build();
        } else {
            // Người dùng chưa đăng nhập → cập nhật cookie
            Map<Long, Integer> cartMap = getCartFromCookie(request);
            if (!cartMap.containsKey(itemId)) {
                return ResponseEntity.badRequest().body("Item not found in cart");
            }

            cartMap.put(itemId, quantity);
            saveCartToCookie(response, cartMap);
            return ResponseEntity.ok().build();
        }
    }






    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @RequestParam("itemId") Long itemId,
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response) {

        Map<String, Object> result = new HashMap<>();
        try {
            if (principal != null) {
                User user = userRepository.findByUsername(principal.getName()).orElseThrow();
                cartService.removeFromCart(user, itemId); // xóa khỏi DB
            } else {
                Map<Long, Integer> cartMap = getCartFromCookie(request);
                cartMap.remove(itemId); // xóa khỏi cookie
                saveCartToCookie(response, cartMap);
            }
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        return ResponseEntity.ok(result);
    }
    @PostMapping("/checkout-selected")
    public String checkoutSelected(@RequestParam("itemIds") List<Long> itemIds,
                                   @RequestParam("total") double total,
                                   HttpServletResponse response) {
        String itemIdsStr = itemIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        Cookie itemCookie = new Cookie("selectedItemIds", itemIdsStr);
        itemCookie.setPath("/");
        itemCookie.setMaxAge(30 * 60);
        response.addCookie(itemCookie);

        Cookie totalCookie = new Cookie("selectedCartTotal", String.format("%.2f", total));
        totalCookie.setPath("/");
        totalCookie.setMaxAge(30 * 60);
        response.addCookie(totalCookie);

        return "redirect:/checkout";
    }


//    @PostMapping("/checkout-selected")
//    public String checkoutSelected(@RequestParam("itemIds") List<Long> itemIds,
//                                   Principal principal,
//                                   HttpServletResponse response) {
//        if (principal == null || itemIds == null || itemIds.isEmpty()) {
//            return "redirect:/cart?error=NoItemsSelected";
//        }
//
//        // Lấy user
//        String username = principal.getName();
//        User user = userRepository.findByUsername(username).orElse(null);
//        if (user == null) return "redirect:/login";
//
//        // Lấy danh sách cart item từ ID
//        List<CartItem> selectedItems = cartService.getCartItemsByIds(itemIds, user);
//
//        // Tính toán subtotal, shipping, total
//        double subtotal = cartService.calculateSubtotal(selectedItems);
//        double shippingFee = cartService.getShippingFee();
//        double total = subtotal + shippingFee;
//
//        // Lưu cả itemIds và total vào cookie
//        saveSelectedItemIdsAndTotalToCookie(response, itemIds, total);
//
//        return "redirect:/checkout";
//    }

    private void saveSelectedItemIdsAndTotalToCookie(HttpServletResponse response, List<Long> ids, double total) {
        // 1. Lưu selectedItemIds
        String itemIds = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining("|"));

        Cookie idCookie = new Cookie("selectedItemIds", itemIds);
        idCookie.setPath("/");
        idCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(idCookie);

        // 2. Lưu total (định dạng 2 chữ số sau dấu chấm)
        Cookie totalCookie = new Cookie("selectedCartTotal", String.format("%.2f", total));
        totalCookie.setPath("/");
        totalCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(totalCookie);
    }

    private void saveSelectedItemIdsToCookie(HttpServletResponse response, List<Long> itemIds) {
        String value = itemIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        Cookie cookie = new Cookie("selectedItemIds", value);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        response.addCookie(cookie);
    }
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
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    return ids;
                }
            }
        }
        return new ArrayList<>();
    }
    private void saveSelectedCouponToCookie(HttpServletResponse response, Long couponId) {
        Cookie cookie = new Cookie("selectedCouponId", String.valueOf(couponId));
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        response.addCookie(cookie);
    }
    private Long getSelectedCouponIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("selectedCouponId".equals(cookie.getName())) {
                    try {
                        return Long.parseLong(cookie.getValue());
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return null;
    }



}
