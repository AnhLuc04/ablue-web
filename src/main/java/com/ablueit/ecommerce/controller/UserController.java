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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
