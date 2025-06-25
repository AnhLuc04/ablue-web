package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.CartItem;
import com.ablueit.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    List<CartItem> findByIdInAndCartUser(List<Long> itemIds, User user);

    List<CartItem> findByUser(User user);
}
