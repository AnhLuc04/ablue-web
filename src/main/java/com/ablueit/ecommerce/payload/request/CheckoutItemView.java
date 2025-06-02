package com.ablueit.ecommerce.payload.request;


public class CheckoutItemView {
    private Long itemId;
    private String productName;
    private double price;
    private int quantity;
    private double subtotal;

    public CheckoutItemView(Long itemId, String productName, double price, int quantity) {
        this.itemId = itemId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = price * quantity;
    }

    // Getters and Setters
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.subtotal = this.price * this.quantity;
    }

    public double getSubtotal() { return subtotal; }
}
