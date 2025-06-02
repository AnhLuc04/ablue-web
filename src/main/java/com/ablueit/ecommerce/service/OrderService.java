package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.*;
import com.ablueit.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CartService cartService;

    public void createOrder(User user, BillingAddress billing, ShippingAddress shipping) {
        Cart cart = cartService.getCartForUser(user);

        Order order = new Order();
        order.setCustomer(user);
       // order.set(LocalDateTime.now());
      //  order.setTotalAmount(cart.getTotal());
        order.setBillingAddress(billing);
        order.setShippingAddress(shipping);

        List<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
           // item.setProduct(cartItem.getVariant().getAttributes());
            item.setQuantity(cartItem.getQuantity());
        //    item.setPrice(cartItem.getProduct().getPrice());
            return item;
        }).collect(Collectors.toList());

        order.setItems(orderItems);
        orderRepo.save(order);

        // Clear cart after placing order
        cart.getItems().clear();
       // cartService.save(cart);
    }
}