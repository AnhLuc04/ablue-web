//package com.ablueit.ecommerce.service;//package com.ablueit.ecommerce.service;
////
////import com.ablueit.ecommerce.model.*;
////import com.ablueit.ecommerce.repository.OrderLogRepository;
////import com.ablueit.ecommerce.repository.OrderRepository;
////import lombok.RequiredArgsConstructor;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Service;
////
////import java.time.LocalDateTime;
////import java.util.List;
////import java.util.Optional;
////import java.util.stream.Collectors;
////@Service
////@RequiredArgsConstructor
////public class OrderService {
////
////    private final OrderRepository orderRepository;
////    private final OrderLogRepository orderLogRepository;
////
////
////    public Order createOrder(Order order) {
////        order.setCreatedAt(LocalDateTime.now());
////        Order savedOrder = orderRepository.save(order);
////
////        // Ghi log đơn hàng mới
////        OrderLog log = OrderLog.builder()
////                .order(savedOrder)
////                .status("CREATED")
////                .note("Order placed")
////                .timestamp(LocalDateTime.now())
////                .build();
////        orderLogRepository.save(log);
////
////        return savedOrder;
////    }
////
////
////    public List<Order> getOrdersByUser(User user) {
////        return orderRepository.findByUser(user);
////    }
////
////    public Optional<Order> getOrderById(Long id) {
////        return orderRepository.findById(id);
////    }
////
////
////    public void updateOrderStatus(Long orderId, String status, String note) {
////        orderRepository.findById(orderId).ifPresent(order -> {
////            order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
////
////            orderRepository.save(order);
////
////            // Ghi log thay đổi trạng thái
////            OrderLog log = OrderLog.builder()
////                    .order(order)
////                    .status(status)
////                    .note(note)
////                    .timestamp(LocalDateTime.now())
////                    .build();
////            orderLogRepository.save(log);
////        });
////    }
////
////    public List<Order> getAllOrders() {
////        return orderRepository.findAll();
////    }
////}
//
//
//import com.ablueit.ecommerce.model.Order;
//import com.ablueit.ecommerce.model.User;
//import com.ablueit.ecommerce.repository.OrderRepository;
//import com.ablueit.ecommerce.repository.ProductRepository;
//import com.ablueit.ecommerce.repository.UserRepository;
//import jakarta.transaction.Transactional;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//public class OrderService {
//
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//    private final OrderRepository orderRepository;
//
//    public OrderService(UserRepository userRepository, ProductRepository productRepository, OrderRepository orderRepository) {
//        this.userRepository = userRepository;
//        this.productRepository = productRepository;
//        this.orderRepository = orderRepository;
//    }
//
//    @Transactional
//    public void createOrder(OrderFormDTO form, String username) throws Exception {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new Exception("User not found"));
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setOrderNote(form.getOrderNote());
//        order.setCreatedAt(LocalDateTime.now());
//        order.setStatus("PENDING");
//
//        // Billing info
//        order.setBillingFirstName(form.getBillingFirstName());
//        order.setBillingLastName(form.getBillingLastName());
//        order.setBillingCountry(form.getBillingCountry());
//        order.setBillingState(form.getBillingState());
//        order.setBillingCity(form.getBillingCity());
//        order.setBillingStreet(form.getBillingStreet());
//        order.setBillingPostcode(form.getBillingPostcode());
//        order.setBillingEmail(form.getBillingEmail());
//        order.setBillingPhone(form.getBillingPhone());
//
//        // Shipping info
//        if (form.isShipDifferent()) {
//            order.setShippingFirstName(form.getShippingFirstName());
//            order.setShippingLastName(form.getShippingLastName());
//            order.setShippingCountry(form.getShippingCountry());
//            order.setShippingState(form.getShippingState());
//            order.setShippingCity(form.getShippingCity());
//            order.setShippingStreet(form.getShippingStreet());
//            order.setShippingPostcode(form.getShippingPostcode());
//            order.setShippingPhone(form.getShippingPhone());
//        } else {
//            order.setShippingFirstName(form.getBillingFirstName());
//            order.setShippingLastName(form.getBillingLastName());
//            order.setShippingCountry(form.getBillingCountry());
//            order.setShippingState(form.getBillingState());
//            order.setShippingCity(form.getBillingCity());
//            order.setShippingStreet(form.getBillingStreet());
//            order.setShippingPostcode(form.getBillingPostcode());
//            order.setShippingPhone(form.getBillingPhone());
//        }
//
//        // Line items
//        List<OrderItem> orderItems = new ArrayList<>();
//        for (int i = 0; i < form.getItemIds().size(); i++) {
//            Long itemId = form.getItemIds().get(i);
//            int quantity = form.getQuantities().get(i);
//
//            ProductVariation variation = productRepository.findVariationById(itemId)
//                    .orElseThrow(() -> new Exception("Product variation not found"));
//
//            OrderItem orderItem = new OrderItem();
//            orderItem.setOrder(order);
//            orderItem.setProductVariation(variation);
//            orderItem.setQuantity(quantity);
//            orderItem.setPrice(variation.getPrice());
//
//            orderItems.add(orderItem);
//        }
//
//        order.setOrderItems(orderItems);
//        orderRepository.save(order);
//    }
//}
