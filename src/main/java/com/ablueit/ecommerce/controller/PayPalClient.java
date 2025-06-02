//package com.ablueit.ecommerce.controller;
//
//import com.paypal.core.PayPalEnvironment;
//import com.paypal.core.PayPalHttpClient;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PayPalClient {
//    private final PayPalHttpClient client;
//
//    public PayPalClient() {
//        PayPalEnvironment environment = new PayPalEnvironment.Sandbox(
//                "YOUR_SANDBOX_CLIENT_ID",
//                "YOUR_SANDBOX_CLIENT_SECRET"
//        );
//        this.client = new PayPalHttpClient(environment);
//    }
//
//    public PayPalHttpClient client() {
//        return client;
//    }
//}
