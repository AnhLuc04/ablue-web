package com.ablueit.ecommerce.enums;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public enum ImageType {
    PRIMARY("primary"),
    SIZE_GUIDE("size_guide"),
    DEFAULT("default");

    String type;

    ImageType(String type) {
        this.type = type;
    }
}

