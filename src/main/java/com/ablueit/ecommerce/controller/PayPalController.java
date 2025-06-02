//package com.ablueit.ecommerce.controller;
//
//import com.ablueit.ecommerce.controller.PayPalClient;
//
//import com.paypal.orders.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/paypal")
//public class PayPalController {
//
//    @Autowired
//    private PayPalClient payPalClient;
//
//    @PostMapping("/create-order")
//    public ResponseEntity<?> createOrder() throws IOException {
//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.checkoutPaymentIntent("CAPTURE");
//
//        ApplicationContext applicationContext = new ApplicationContext()
//                .brandName("My Shop")
//                .landingPage("NO_PREFERENCE")
//                .cancelUrl("http://localhost:8080/checkout/cancel")
//                .returnUrl("http://localhost:8080/checkout/success")
//                .userAction("PAY_NOW");
//
//        List<PurchaseUnitRequest> purchaseUnits = new ArrayList<>();
//        purchaseUnits.add(new PurchaseUnitRequest()
//                .amountWithBreakdown(new AmountWithBreakdown()
//                        .currencyCode("USD")
//                        .value("100.00") // Giá cố định ở đây hoặc lấy từ frontend/cart
//                ));
//
//        orderRequest.applicationContext(applicationContext);
//        orderRequest.purchaseUnits(purchaseUnits);
//
//        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
//        Order order = payPalClient.client().execute(request).result();
//
//        return ResponseEntity.ok(Collections.singletonMap("id", order.id()));
//    }
//
//    @PostMapping("/capture-order/{orderId}")
//    public ResponseEntity<?> captureOrder(@PathVariable String orderId) throws IOException {
//        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
//        request.requestBody(new OrderRequest());
//
//        Order order = payPalClient.client().execute(request).result();
//        return ResponseEntity.ok(order);
//    }
//}
