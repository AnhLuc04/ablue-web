package com.ablueit.ecommerce.model;

public enum OrderStatus {
    PENDING,     // Đang chờ xử lý
    PROCESSING,  // Đang xử lý
    SHIPPED,     // Đã giao cho đơn vị vận chuyển
    DELIVERED,   // Đã giao thành công
    CANCELLED,   // Đã hủy
    REFUNDED     // Đã hoàn tiền
}
