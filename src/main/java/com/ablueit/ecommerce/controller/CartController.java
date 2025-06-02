package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.model.Cart;
import com.ablueit.ecommerce.model.DiscountCode;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.payload.request.CartItemRequest;
import com.ablueit.ecommerce.payload.request.CheckoutItemView;
import com.ablueit.ecommerce.repository.DiscountCodeRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import com.ablueit.ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @GetMapping
    public ModelAndView showCart(Principal principal) {
        ModelAndView modelAndView = new ModelAndView();

        Optional<User> userOptional = userRepository.findByUsername(principal.getName());
        if (userOptional.isEmpty()) {
            modelAndView.setViewName("redirect:/login?error=userNotFound");
            return modelAndView;
        }

        Cart cart = cartService.getCartForUser(userOptional.get());

        modelAndView.setViewName("cart/view");
        modelAndView.addObject("cart", cart);
        modelAndView.addObject("totalPrice", cart.getTotal());

        return modelAndView;
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long variantId, @RequestParam int quantity, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        cartService.addToCart(user, variantId, quantity); // sửa thành variantId
        return "redirect:/cart";
    }

    @PostMapping("/update-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateItemAjax(
            @RequestParam("itemId") Long itemId,
            @RequestParam("action") String action,
            Principal principal) {

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Cart cart = cartService.getCartForUser(user);

        cartService.updateItemQuantity(cart, itemId, action);

        BigDecimal totalPrice = cartService.calculateTotal(cart);
        int updatedQty = cartService.getQuantityForItem(cart, itemId);

        Map<String, Object> response = new HashMap<>();
        response.put("quantity", updatedQty);
        response.put("totalPrice", totalPrice);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove-unchecked")
    @ResponseBody
    public ResponseEntity<?> removeUnchecked(@RequestBody List<Long> uncheckedIds, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        cartService.removeUnchecked(user, uncheckedIds);
        return ResponseEntity.ok().build();
    }



    @GetMapping("/discounts")
    @ResponseBody
    public List<DiscountCode> getAllActiveDiscountsForUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return discountCodeRepository.findByActiveTrueAndUserUsername(username);
    }

    // Nếu cần thêm xử lý mã giảm giá sau này
}


//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//import lombok.RequiredArgsConstructor;
//@Controller
//@RequestMapping("/cart")
//@RequiredArgsConstructor
//public class CartController {
//
//    private final CartService cartService;
//    private final UserRepository userRepository;
//
//    // Thêm sản phẩm (variation) vào giỏ
//    @PostMapping("/add")
//    public String addToCart(@RequestParam Long variationId,
//                            @RequestParam(defaultValue = "1") int quantity,
//                            Principal principal) {
//
//        User user = userRepository.findByUsername(principal.getName())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        cartService.addToCart(user, variationId, quantity);
//        return "redirect:/cart"; // Sau khi thêm, chuyển đến trang hiển thị giỏ hàng
//    }
//
//    // Hiển thị giỏ hàng
//    @GetMapping
//    public String showCart(Model model, Principal principal) {
//        User user = userRepository.findByUsername(principal.getName())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        Cart cart = cartService.getCartForUser(user);
//        model.addAttribute("cart", cart);
//        model.addAttribute("total", cartService.calculateTotal(cart));
//
//        return "cart/view"; // Trả về view Thymeleaf: resources/templates/cart/view.html
//    }
//
////    // Cập nhật số lượng sản phẩm trong giỏ
////    @PostMapping("/update")
////    public String updateQuantity(@RequestParam Long itemId,
////                                 @RequestParam int quantity,
////                                 Principal principal) {
////
////        User user = userRepository.findByUsername(principal.getName())
////                .orElseThrow(() -> new RuntimeException("User not found"));
////
////        cartService.updateQuantity(user, itemId, quantity);
////        return "redirect:/cart";
////    }
//
////    // Xóa sản phẩm khỏi giỏ
////    @PostMapping("/remove")
////    public String removeItem(@RequestParam Long itemId,
////                             Principal principal) {
////        User user = userRepository.findByUsername(principal.getName())
////                .orElseThrow(() -> new RuntimeException("User not found"));
////
////        cartService.removeItem(user, itemId);
////        return "redirect:/cart";
////    }
//}
