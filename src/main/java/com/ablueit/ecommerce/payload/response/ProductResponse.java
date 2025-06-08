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

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductShortDescription() {
        return productShortDescription;
    }

    public void setProductShortDescription(String productShortDescription) {
        this.productShortDescription = productShortDescription;
    }

    public Double getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(Double regularPrice) {
        this.regularPrice = regularPrice;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Long stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public String getBackorders() {
        return backorders;
    }

    public void setBackorders(String backorders) {
        this.backorders = backorders;
    }

    public List<VariationResponse> getVariationsData() {
        return variationsData;
    }

    public void setVariationsData(List<VariationResponse> variationsData) {
        this.variationsData = variationsData;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public String getSizeGuideImage() {
        return sizeGuideImage;
    }

    public void setSizeGuideImage(String sizeGuideImage) {
        this.sizeGuideImage = sizeGuideImage;
    }

    public List<String> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(List<String> galleryImages) {
        this.galleryImages = galleryImages;
    }
}
