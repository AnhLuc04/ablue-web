package com.ablueit.ecommerce.payload.request;

import com.ablueit.ecommerce.model.BillingAddress;
import com.ablueit.ecommerce.model.ShippingAddress;

public class CheckoutForm {
    private BillingAddressDTO billing;
    private ShippingAddressDTO shipping;

    private Long customerId;
    private Long storeId;
    private String paymentMethod;
    private String note;

    public BillingAddressDTO getBilling() {
        return billing;
    }

    public void setBilling(BillingAddressDTO billing) {
        this.billing = billing;
    }

    public ShippingAddressDTO getShipping() {
        return shipping;
    }

    public void setShipping(ShippingAddressDTO shipping) {
        this.shipping = shipping;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
