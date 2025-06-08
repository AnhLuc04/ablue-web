package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.exception.ResourceNotFoundException;
import com.ablueit.ecommerce.model.Categories;
import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.model.Store;
import com.ablueit.ecommerce.payload.request.ProductRequest;
import com.ablueit.ecommerce.payload.response.ProductResponse;
import com.ablueit.ecommerce.repository.CategoriesRepository;
import com.ablueit.ecommerce.repository.ProductRepository;
import com.ablueit.ecommerce.repository.StoreRepository;
import com.ablueit.ecommerce.service.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j(topic = "PRODUCT-CONTROLLER")
@RequestMapping("/products")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    StoreRepository storeRepository;
    CategoriesRepository categoriesRepository;
    ProductService productService;
    ProductRepository productRepository;


    @GetMapping("/{id}")
    public String viewProductList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @PathVariable(value = "id") Long storeId,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage;

        if (storeId != null) {
            productPage = productRepository.findByStoreId(storeId, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<Store> stores = storeRepository.findAll();

        model.addAttribute("productPage", productPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("storeId", storeId);
        model.addAttribute("stores", stores);

        return "product-dashboard/product-list";
    }




    @GetMapping("/create-variation-product/{id}")
    public String showCreateVariationProduct(Model model, @PathVariable(value = "id") Long storeId) {
        log.info("GET /create-variation-product/{}", storeId);

        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResourceNotFoundException("store not found"));

        List<String> categories = categoriesRepository.findByStore(store).stream().map(Categories::getName).toList();

        model.addAttribute("storeId", store.getId());

        model.addAttribute("categories", categories);

        return "product-dashboard/create-variation-product";
    }




    @PostMapping("/create-variation-product/")
    public ResponseEntity<?> createVariationProduct(@ModelAttribute  ProductRequest request) throws IOException {
        return ResponseEntity.ok().body(productService.addVariationProduct(request));
    }


    @GetMapping("/edit-variation-product/{id}")
    public String showEditVariationProduct(@PathVariable("id") Long id, Model model) {
        log.info("GET /edit-variation-product/{}", id);

        // Tìm sản phẩm theo ID
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sản phẩm với ID: " + id));

        // Lấy danh sách danh mục từ store liên kết với sản phẩm
        Store store = product.getStore();
        List<Categories> categories = categoriesRepository.findByStore(store);

        // Đưa dữ liệu vào model để render ra Thymeleaf view
        model.addAttribute("storeId", store.getId());
        model.addAttribute("categories", categories);
        model.addAttribute("product", product); // Nếu bạn dùng product trong view (Thymeleaf)

        // Đẩy dữ liệu binding theo kiểu DTO nếu cần
        ProductRequest request = productService.mapToRequest(product);
        model.addAttribute("productRequest", request);

        return "product-dashboard/edit-variation-product"; // tên file HTML ở /templates/product-dashboard
    }

    @PostMapping("/edit-variation-product")
    public ResponseEntity<?> updateVariationProduct(@ModelAttribute ProductRequest request) throws IOException {
        String result = productService.updateVariationProduct(request);
        return ResponseEntity.ok().body(result);
    }



    @GetMapping("/show-product/{id}")
    public String showProduct(Model model, @PathVariable Long id) throws IOException {
        log.info("GET /show-product/{}", id);
        model.addAttribute("productId", id);
        return "product-dashboard/show-product";
    }
    @GetMapping(value = "/get-product/{id}", produces = "application/json")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
       log.info("GET /get-product/{}", id);
        return ResponseEntity.ok().body(productService.getProduct(id));
    }
}