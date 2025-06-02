package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.model.CartItem;
import com.ablueit.ecommerce.model.Order;
import com.ablueit.ecommerce.payload.request.CartItemRequest;
import com.ablueit.ecommerce.payload.request.CheckoutForm;
import com.ablueit.ecommerce.payload.request.CheckoutItemView;
import com.ablueit.ecommerce.repository.DiscountCodeRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import com.ablueit.ecommerce.service.CartService;
//import com.ablueit.ecommerce.service.PayPalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Controller
@RequestMapping("/checkout")
public class CheckoutController {
//
//    @Autowired
//    private CartService cartService;
//
//    @Autowired
//    private PayPalService payPalService;
//
//    @GetMapping("/cart")
//    public String viewCart(
//            @RequestParam(value = "selectedItems", required = false) List<Long> selectedItemIds,
//            Model model,
//            Principal principal
//    ) {
//        List<CartItem> cartItems;
//        BigDecimal cartTotal;
//
//        if (selectedItemIds != null && !selectedItemIds.isEmpty()) {
//            // Trường hợp 1: người dùng chọn sản phẩm cụ thể
//            cartItems = cartService.getItemsByIds(selectedItemIds);
//            cartTotal = cartService.calculateTotal(cartItems);
//        } else {
//            // Trường hợp 2: không truyền gì, lấy giỏ hàng theo user
//            String username = principal.getName();
//            cartItems = cartService.getCartByUsername(username);
//            cartTotal = cartService.calculateTotal(cartItems);
//        }
//
//        model.addAttribute("cartItems", cartItems);
//        model.addAttribute("cartTotal", cartTotal);
//        return "cart";
//    }
//
//    @PostMapping("/checkout")
//    public String processCheckout(@ModelAttribute CheckoutForm checkoutForm, RedirectAttributes redirectAttributes) {
//        // Xử lý lưu đơn hàng
//        Order savedOrder = cartService.saveOrder(checkoutForm);
//        redirectAttributes.addFlashAttribute("message", "Đơn hàng đã được ghi nhận!");
//        return "redirect:/checkout/success";
//    }
//
//    @GetMapping("/checkout/success")
//    public String checkoutSuccessPage() {
//        return "success";
//    }
//
//    // --- API: tạo order PayPal ---
//    @PostMapping("/api/paypal/create-order")
//    public ResponseEntity<Map<String, String>> createPayPalOrder() {
//        String orderId = payPalService.createOrder();
//        return ResponseEntity.ok(Map.of("id", orderId));
//    }
//
//    // --- API: xác nhận order PayPal ---
//    @PostMapping("/api/paypal/capture-order/{orderId}")
//    public ResponseEntity<?> captureOrder(@PathVariable String orderId) {
//        Map<String, Object> result = payPalService.captureOrder(orderId);
//        return ResponseEntity.ok(result);
//    }






    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiscountCodeRepository discountCodeRepository;


    @PostMapping
    public ResponseEntity<?> handleCheckout(@RequestBody List<CartItemRequest> items, HttpSession session) {
        session.setAttribute("checkoutItems", items);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ModelAndView showCheckoutPage(HttpSession session) {
        List<CartItemRequest> checkoutItems = (List<CartItemRequest>) session.getAttribute("checkoutItems");

        if (checkoutItems == null || checkoutItems.isEmpty()) {
            return new ModelAndView("redirect:/cart");
        }

        List<CheckoutItemView> viewItems = cartService.buildCheckoutItems(checkoutItems);

        ModelAndView mav = new ModelAndView("checkout/form");
        mav.addObject("items", viewItems);
        return mav;
    }
}
