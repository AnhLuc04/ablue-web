package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.model.Categories;
import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.model.Store;
import com.ablueit.ecommerce.repository.CategoriesRepository;
import com.ablueit.ecommerce.repository.ProductRepository;
import com.ablueit.ecommerce.repository.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/")
    public String home(Model model) {
        List<Product> topProducts = productRepository.findTop8ByOrderBySalesCountDesc();
        List<Store> stores = storeRepository.findAll();

        List<Categories> randomCategories = categoriesRepository.findAll().stream().limit(10).toList();

        model.addAttribute("categories", randomCategories);
        model.addAttribute("products", topProducts);
        model.addAttribute("stores", stores);

        return "index"; // Trả về file index.html trong templates
    }
    @GetMapping("/stores")
    public String listStores(Model model) {
        model.addAttribute("stores", storeRepository.findAll());
        return "store-dashboard/store-list";  // Thymeleaf template name
    }
    @GetMapping("/{storeId}/products")
    public String getProductsByStore(@PathVariable Long storeId, Model model) {
        List<Product> products = productRepository.findByStoreId(storeId);
        model.addAttribute("products", products);
        return "store/store-products";
    }
}
