package com.ablueit.ecommerce.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRequest {
    String name;
    String description;
    String shortDescription;
    Double regularPrice;
    Double salePrice;
    Long categoryId;
    Long storeId;
    String sku;
    Integer stockQuantity;
    String stockStatus;
    List<VariationRequest> variationsData;
    @ToString.Exclude @JsonProperty("primaryImage") MultipartFile primaryImage;
    @ToString.Exclude @JsonProperty("sizeGuideImage") MultipartFile sizeGuideImage;
    @ToString.Exclude @JsonProperty("galleryImages") List<MultipartFile> galleryImages;
}
