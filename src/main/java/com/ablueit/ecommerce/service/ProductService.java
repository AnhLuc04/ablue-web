package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.payload.request.ProductRequest;
import com.ablueit.ecommerce.payload.response.ProductResponse;

import java.io.IOException;

public interface ProductService {

    String addVariationProduct(ProductRequest request) throws IOException;
    ProductRequest mapToRequest(Product product); // ✅ Thêm dòng này
    String updateVariationProduct(ProductRequest request) throws  IOException;
    ProductResponse getProduct(Long id);
}
