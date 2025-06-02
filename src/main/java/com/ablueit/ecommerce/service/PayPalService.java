//package com.ablueit.ecommerce.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//;
//import java.util.Base64;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class PayPalService {
//
//    @Value("${paypal.client-id}")
//    private String clientId;
//
//    @Value("${paypal.secret}")
//    private String secret;
//
//    @Value("${paypal.api-base}")
//    private String apiBase;
//
//    private String accessToken;
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @PostConstruct
//    public void init() {
//        this.accessToken = fetchAccessToken();
//    }
//
//    public String fetchAccessToken() {
//        String auth = clientId + ":" + secret;
//        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        headers.set("Authorization", "Basic " + encodedAuth);
//
//        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//        body.add("grant_type", "client_credentials");
//
//        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//        RestTemplate restTemplate = new RestTemplate();
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(apiBase + "/v1/oauth2/token", request, Map.class);
//
//        if (response.getStatusCode() == HttpStatus.OK) {
//            Map<String, Object> responseBody = response.getBody();
//            return (String) responseBody.get("access_token");
//        }
//
//        throw new RuntimeException("Failed to fetch access token");
//    }
//
//    public String createOrder() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        Map<String, Object> orderRequest = new HashMap<>();
//        orderRequest.put("intent", "CAPTURE");
//
//        Map<String, Object> purchaseUnit = new HashMap<>();
//        purchaseUnit.put("amount", Map.of(
//                "currency_code", "USD",
//                "value", "100.00" // Có thể thay bằng tổng giỏ hàng
//        ));
//        orderRequest.put("purchase_units", new Object[]{purchaseUnit});
//
//        Map<String, Object> applicationContext = new HashMap<>();
//        applicationContext.put("return_url", "http://localhost:8080/checkout/success");
//        applicationContext.put("cancel_url", "http://localhost:8080/checkout/cancel");
//        orderRequest.put("application_context", applicationContext);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, headers);
//
//        ResponseEntity<Map> response = restTemplate.postForEntity(
//                apiBase + "/v2/checkout/orders",
//                request,
//                Map.class
//        );
//
//        if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
//            return (String) response.getBody().get("id");
//        }
//
//        throw new RuntimeException("Không thể tạo order PayPal");
//    }
//
//    public Map<String, Object> captureOrder(String orderId) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> request = new HttpEntity<>(null, headers);
//
//        ResponseEntity<Map> response = restTemplate.exchange(
//                apiBase + "/v2/checkout/orders/" + orderId + "/capture",
//                HttpMethod.POST,
//                request,
//                Map.class
//        );
//
//        if (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null) {
//            return response.getBody();
//        }
//
//        throw new RuntimeException("Không thể xác nhận order PayPal");
//    }
//}
