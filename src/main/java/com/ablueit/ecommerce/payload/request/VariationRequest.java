package com.ablueit.ecommerce.payload.request;

import java.io.Serializable;
import java.util.List;

public record VariationRequest(
        List<AttributeRequest> attributes,
        Double price,
        Integer stock
) implements Serializable  {

    public record AttributeRequest(
            String name,
            String term
    ) {

    }

    public record AddToCartRequest(
            Long variantId,
            int quantity
    ) {}

}
