package com.ablueit.ecommerce.enums;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public enum ProductStatus {
    DRAFT("draft"),
    PENDING("pending"),
    PRIVATE("private"),
    PUBLISHED("published");

    String status;

    ProductStatus(String status) {
        this.status = status;
    }
}

