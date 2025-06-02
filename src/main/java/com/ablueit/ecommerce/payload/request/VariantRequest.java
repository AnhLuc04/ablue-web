package com.ablueit.ecommerce.payload.request;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class VariantRequest {
    private String type;  // Loại biến thể (Màu sắc, Kích thước)
    private String value; // Giá trị biến thể (Red, Black, M, L)

    private BigDecimal price;
    private BigDecimal listPrice;
    private Integer quantity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
