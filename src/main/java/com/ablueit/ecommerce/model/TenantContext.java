package com.ablueit.ecommerce.model;

public class TenantContext {
    private static final ThreadLocal<Store> currentTenant = new ThreadLocal<>();

    public static void setCurrentTenant(Store store) {
        currentTenant.set(store);
    }

    public static Store getCurrentTenant() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}
