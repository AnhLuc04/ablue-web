package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.payload.request.ProductRequest;
import com.ablueit.ecommerce.payload.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface ProductService {

    ProductResponse addVariationProduct(ProductRequest request) throws IOException;

    ProductRequest mapToRequest(Product product);

    String updateVariationProduct(ProductRequest request) throws IOException;

    ProductResponse getProduct(Long id);

    Page<Product> searchProducts(String keyword, String category, Double maxPrice, String sort, Pageable pageable);
}
