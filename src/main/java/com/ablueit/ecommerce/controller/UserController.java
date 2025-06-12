package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.enums.ImageType;
import com.ablueit.ecommerce.model.Categories;
import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.model.ProductImage;
import com.ablueit.ecommerce.model.Store;
import com.ablueit.ecommerce.payload.response.ProductCardResponse;
import com.ablueit.ecommerce.repository.CategoriesRepository;
import com.ablueit.ecommerce.repository.ProductImageRepository;
import com.ablueit.ecommerce.repository.ProductRepository;
import com.ablueit.ecommerce.repository.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/")
    public String home(Model model) {
        List<Product> topProducts = productRepository.findTop8ByOrderBySalesCountDesc();

        List<ProductCardResponse> cards = topProducts.stream().map(x -> {
            ProductImage primaryImage = productImageRepository
                    .findByImageTypeAndProduct(ImageType.PRIMARY, x);

            return ProductCardResponse.builder()
                    .id(x.getId())
                    .name(x.getName())
                    .price(x.getPrice())
                    .rating(4.5)
                    .primaryImage(primaryImage.getUrl())
                    .totalSold(100L)
                    .build();
        }).toList();

        List<Store> stores = storeRepository.findAll();

        List<Categories> randomCategories = categoriesRepository.findAll().stream().limit(10).toList();

        model.addAttribute("categories", randomCategories);
        model.addAttribute("products", cards);
        model.addAttribute("stores", stores);

        return "index"; // Trả về file index.html trong templates
    }
    @GetMapping("/stores")
    public String listStores(Model model) {
        model.addAttribute("stores", storeRepository.findAll());
        return "store-dashboard/store-list";  // Thymeleaf template name
    }
//    @GetMapping("/{storeId}/products")
//    public String getProductsByStore(
//            @PathVariable("storeId") Long storeId,
//            @RequestParam(name = "category", defaultValue = "") String category,
//            @RequestParam(name = "sort", defaultValue = "default") String sort,
//            @RequestParam(name = "minPrice", defaultValue = "0") double minPrice,
//            @RequestParam(name = "maxPrice", defaultValue = "100000000") double maxPrice,
//            @RequestParam(name = "page", defaultValue = "0") int page,
//            @RequestParam(name = "size", defaultValue = "12") int size,
//            Model model) {
//        Pageable pageable;
//        switch (sort) {
//            case "best_selling":
//                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "salesCount"));
//                break;
//            case "newest":
//                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//                break;
//            case "most_favorite":
//                pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "favoriteCount"));
//                break;
//            default:
//                pageable = PageRequest.of(page, size);
//        }
//
//        Page<Product> products;
//
//        if (category.isEmpty()) {
//            products = productRepository.findByPriceBetweenAndStoreId(minPrice, maxPrice, storeId, pageable);
//        } else {
//            products = productRepository.findByCategoryNameAndPriceBetweenAndStoreId(category, minPrice, maxPrice, storeId, pageable);
//        }
//
//        model.addAttribute("products", products);
//        model.addAttribute("categories", categoriesRepository.findAll());
//        model.addAttribute("currentPage", page);
//        model.addAttribute("totalPages", products.getTotalPages());
//        model.addAttribute("selectedCategory", category);
//        model.addAttribute("selectedSort", sort);
//        model.addAttribute("minPrice", minPrice);
//        model.addAttribute("maxPrice", maxPrice);
//        model.addAttribute("size", size);
//        model.addAttribute("storeId", storeId);
//
//        return "store-dashboard/store-products";
//    }
@GetMapping("/{storeId}/products")
public String getProductsByStore(
        @PathVariable("storeId") Long storeId,
        @RequestParam(name = "category", defaultValue = "") String category,
        @RequestParam(name = "sort", defaultValue = "default") String sort,
        @RequestParam(name = "minPrice", defaultValue = "0") double minPrice,
        @RequestParam(name = "maxPrice", defaultValue = "100000000") double maxPrice,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "12") int size,
        Model model) {

    Pageable pageable;
    switch (sort) {
        case "best_selling":
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "salesCount"));
            break;
        case "newest":
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            break;
        case "most_favorite":
            pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "favoriteCount"));
            break;
        default:
            pageable = PageRequest.of(page, size);
    }

    Page<Product> products;

    if (category.isEmpty()) {
        products = productRepository.findByPriceBetweenAndStoreId(minPrice, maxPrice, storeId, pageable);
    } else {
        products = productRepository.findByCategoryNameAndPriceBetweenAndStoreId(
                category, minPrice, maxPrice, storeId, pageable);
    }

    model.addAttribute("products", products);
    model.addAttribute("categories", categoriesRepository.findAll());
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", products.getTotalPages());
    model.addAttribute("selectedCategory", category);
    model.addAttribute("selectedSort", sort);
    model.addAttribute("minPrice", minPrice);
    model.addAttribute("maxPrice", maxPrice);
    model.addAttribute("size", size);
    model.addAttribute("storeId", storeId);

    return "store-dashboard/store-products";
}
    @GetMapping("/test")
    public String test(Model model) {

        return "test";  // Thymeleaf template name
    }
}
