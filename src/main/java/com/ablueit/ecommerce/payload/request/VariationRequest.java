package com.ablueit.ecommerce.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VariationRequest {
    private List<AttributeRequest> attributes;
    private Double price;
    private Integer stock;
}