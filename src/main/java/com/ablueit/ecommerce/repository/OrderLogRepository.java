package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {
}
