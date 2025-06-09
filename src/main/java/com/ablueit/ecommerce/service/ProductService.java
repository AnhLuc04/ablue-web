package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.payload.request.ProductRequest;
import com.ablueit.ecommerce.payload.response.ProductCardResponse;
import com.ablueit.ecommerce.payload.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    String addVariationProduct(ProductRequest request) throws IOException;

    ProductRequest mapToRequest(Product product);

    String updateVariationProduct(ProductRequest request) throws IOException;

    ProductResponse getProduct(Long id);

    List<ProductCardResponse> getProductCardByCategory(Long categoryId, Long productId);

    Page<Product> searchProducts(String keyword, String category, Double maxPrice, String sort, Pageable pageable);
}
