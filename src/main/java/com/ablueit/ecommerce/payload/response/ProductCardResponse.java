package com.ablueit.ecommerce.payload.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductCardResponse {
    Long id;
    String name;
    Double price;
    Long totalSold;
    Double rating;
    String productUrl;
    String primaryImage;
}
