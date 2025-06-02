package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Thêm phương thức truy vấn tùy chỉnh nếu cần, ví dụ:
    // List<Order> findByUser(User user);
}
