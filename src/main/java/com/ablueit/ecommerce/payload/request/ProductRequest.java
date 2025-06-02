package com.ablueit.ecommerce.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ProductRequest(
        @JsonProperty("productName") String name,
        @JsonProperty("productDescription") String description,
        @JsonProperty("productShortDescription") String shortDescription,
        @JsonProperty("regularPrice") Double regularPrice,
        @JsonProperty("salePrice") Double salePrice,
        @JsonProperty("category") String category,
        @JsonProperty("storeId") Long storeId,
        @JsonProperty("sku") String sku,
        @JsonProperty("stockQuantity") Integer stockQuantity,
        @JsonProperty("stockStatus") String stockStatus,
        @JsonProperty("variationsData") List<VariationRequest> variationsData,

        // 🔁 CHUYỂN TỪ String → MultipartFile
        @ToString.Exclude @JsonProperty("primaryImage") MultipartFile primaryImage,
        @ToString.Exclude @JsonProperty("sizeGuideImage") MultipartFile sizeGuideImage,
        @ToString.Exclude @JsonProperty("galleryImages") List<MultipartFile> galleryImages
) {}
