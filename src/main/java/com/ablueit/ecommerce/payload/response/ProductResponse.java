package com.ablueit.ecommerce.payload.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductResponse {

    Long storeId;
    String productName;
    String productDescription;
    String productShortDescription;
    Double regularPrice;
    Double salePrice;
    String category;
    String sku;
    Long stockQuantity;
    String stockStatus;
    String backorders;
    List<VariationResponse> variationsData;
    String primaryImage;
    String sizeGuideImage;
    List<String> galleryImages;
}
