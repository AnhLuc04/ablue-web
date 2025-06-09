package com.ablueit.ecommerce.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttributeRequest {
    private String name;
    private String term;
}
