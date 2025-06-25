package com.ablueit.ecommerce.model;

import org.springframework.stereotype.Component;

@Component
public class StoreContext {
    private static final ThreadLocal<Store> currentStore = new ThreadLocal<>();

    public void setStore(Store store) {
        currentStore.set(store);
    }

    public Store getCurrentStore() {
        return currentStore.get();
    }

    public void clear() {
        currentStore.remove();
    }
}
