package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.enums.ImageType;
import com.ablueit.ecommerce.model.*;
import com.ablueit.ecommerce.payload.response.ProductCardResponse;
import com.ablueit.ecommerce.payload.response.ProductResponse;
import com.ablueit.ecommerce.repository.CategoriesRepository;
import com.ablueit.ecommerce.repository.ProductImageRepository;
import com.ablueit.ecommerce.repository.ProductRepository;
import com.ablueit.ecommerce.repository.StoreRepository;
import com.ablueit.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageRepository productImageRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @GetMapping("/")
    public String home(Model model, HttpServletRequest request) {
        String domain = request.getServerName(); // Ví dụ: store1.local

        Optional<Store> storeOpt = storeRepository.findByDomainJPQL(domain);

        if (storeOpt.isPresent()) {
            Store store = storeOpt.get();

            // Lấy top sản phẩm của store
            List<Product> topProducts = productRepository.findTop8ByStoreOrderBySalesCountDesc(store);
            List<ProductCardResponse> cards = topProducts.stream().map(x -> {
                ProductImage primaryImage = productImageRepository
                        .findByImageTypeAndProduct(ImageType.PRIMARY, x);

                return ProductCardResponse.builder()
                        .id(x.getId())
                        .name(x.getName())
                        .price(x.getPrice())
                        .rating(4.5)
                        .primaryImage(primaryImage != null ? primaryImage.getUrl() : "")
                        .totalSold(100L)
                        .build();
            }).toList();

            List<Categories> storeCategories = categoriesRepository.findByStore(store);

            model.addAttribute("store", store);
            model.addAttribute("products", cards);
            model.addAttribute("categories", storeCategories);

            // 👉 Trả về giao diện theo template name
            String templateName = store.getTemplate().getHome();
//            if (store.getTemplate() != null && Template.isBlank(store.getTemplate().getHome())) {
//                templateName = store.getTemplate().getName().trim(); // Ví dụ: "template1"
//            }

            return "view-store/"+templateName;
        }

        // Nếu là domain tổng (localhost...) → trả về trang chủ chung
        List<Product> topProducts = productRepository.findTop8ByOrderBySalesCountDesc();
        List<ProductCardResponse> cards = topProducts.stream().map(x -> {
            ProductImage primaryImage = productImageRepository
                    .findByImageTypeAndProduct(ImageType.PRIMARY, x);

            return ProductCardResponse.builder()
                    .id(x.getId())
                    .name(x.getName())
                    .price(x.getPrice())
                    .rating(4.5)
                    .primaryImage(primaryImage != null ? primaryImage.getUrl() : "")
                    .totalSold(100L)
                    .build();
        }).toList();

        List<Store> stores = storeRepository.findAll();
        List<Categories> randomCategories = categoriesRepository.findAll().stream().limit(10).toList();

        model.addAttribute("categories", randomCategories);
        model.addAttribute("products", cards);
        model.addAttribute("stores", stores);

        return "index";
    }




    @GetMapping("/products")
    public String listProducts(
            @RequestParam(name = "category", defaultValue = "") String category,
            @RequestParam(name = "sort", defaultValue = "default") String sort,
            @RequestParam(name = "minPrice", defaultValue = "0") double minPrice,
            @RequestParam(name = "maxPrice", defaultValue = "100000000") double maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            HttpServletRequest request,
            Model model
    ) {
        // ➤ Xác định domain
        String host = request.getServerName(); // ví dụ: store1.mysite.com hoặc mysite.com
        Optional<Store> optionalStore = storeRepository.findByDomain(host);

        if (optionalStore.isEmpty()) {
            return "redirect:/error"; // Không tìm thấy Store ứng với domain
        }

        Store store = optionalStore.get();

        // ➤ Phân trang + sắp xếp
        Pageable pageable = switch (sort) {
            case "best_selling" -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "salesCount"));
            case "newest" -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            case "most_favorite" -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "favoriteCount"));
            default -> PageRequest.of(page, size);
        };

        // ➤ Truy vấn sản phẩm theo store + category + price
        Page<Product> productPage;
        if (category.isEmpty()) {
            productPage = productRepository.findByPriceBetweenAndStore(
                    minPrice, maxPrice, store, pageable
            );
        } else {
            productPage = productRepository.findByCategoryNameAndPriceBetweenAndStoreId(
                    category, minPrice, maxPrice, store.getId(), pageable
            );
        }

        // ➤ Chuyển Product → ProductCardResponse
        Page<ProductCardResponse> productCards = productPage.map(product -> {
            ProductImage primaryImage = productImageRepository.findByImageTypeAndProduct(ImageType.PRIMARY, product);

            return ProductCardResponse.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .price(product.getPrice())
                    .rating(4.5)
                    .primaryImage(primaryImage != null ? primaryImage.getUrl() : "")
                    .totalSold(product.getSalesCount())
                    .build();
        });

        // ➤ Gửi dữ liệu về view
        model.addAttribute("products", productCards);
        model.addAttribute("categories", categoriesRepository.findByStore(store));
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productCards.getTotalPages());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("size", size);

        return "product-list-user";
    }
    @GetMapping("/product/search")
    public String viewProducts(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "category", required = false, defaultValue = "") String category,
            @RequestParam(name = "maxPrice", required = false, defaultValue = "100000000") Double maxPrice,
            @RequestParam(name = "sort", required = false, defaultValue = "default") String sort,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            HttpServletRequest request,
            Model model) {

        String domain = request.getServerName(); // Ví dụ: store1.local
        Optional<Store> storeOpt = storeRepository.findByDomain(domain);

        Pageable pageable;
        switch (sort) {
            case "best_selling" -> pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "salesCount"));
            case "newest" -> pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            case "most_favorite" -> pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "favoriteCount"));
            default -> pageable = PageRequest.of(page, size);
        }

        Page<Product> products;

        if (storeOpt.isPresent()) {
            // 👉 Nếu có store → tìm kiếm trong store đó
            Store store = storeOpt.get();
            products = productRepository.searchProductsByStore(keyword, category, maxPrice, sort, store, pageable);
            model.addAttribute("categories", categoriesRepository.findByStore(store));
            model.addAttribute("store", store);
        } else {
            // 👉 Domain tổng → tìm kiếm toàn bộ sản phẩm
            products = productService.searchProducts(keyword, category, maxPrice, sort, pageable);
            model.addAttribute("categories", categoriesRepository.findAll());
        }

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("size", size);

        return "search-product-user"; // HTML template hiển thị kết quả tìm kiếm
    }

    @GetMapping("/products/show-product/{id}")
    public String showProduct(Model model, @PathVariable Long id) throws IOException {

        model.addAttribute("productId", id);
        return "show-product-user";
    }

    @GetMapping(value = "/products/get-product/{id}", produces = "application/json")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {

        return ResponseEntity.ok().body(productService.getProduct(id));
    }

    @GetMapping(value = "/products/get-related-product/{category-id}", produces = "application/json")
    public ResponseEntity<List<ProductCardResponse>> getRelatedProduct(@PathVariable("category-id") Long id) {

        return ResponseEntity.ok().body(productService.getProductCardByCategory(id, 1L));
    }
}

