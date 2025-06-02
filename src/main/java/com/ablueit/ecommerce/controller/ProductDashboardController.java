//package com.ablueit.ecommerce.controller;
//
//import com.ablueit.ecommerce.exception.ResourceNotFoundException;
//import com.ablueit.ecommerce.model.*;
//import com.ablueit.ecommerce.payload.request.ProductRequest;
//import com.ablueit.ecommerce.payload.request.ProductService;
//import com.ablueit.ecommerce.payload.request.VariantRequest;
//import com.ablueit.ecommerce.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.*;
//
//import org.springframework.web.multipart.MultipartFile;
//
//import java.math.BigDecimal;
//
//@Controller
//@RequestMapping("/products/dashboard")
//public class ProductDashboardController {
//
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private VariantRepository variantRepository;
//    @Autowired
//    private ProductVariationRepository productVariationRepository;
//
//    @Autowired
//    private CategoriesRepository categoryRepository;
//
//    @Autowired
//    private StoreRepository storeRepository;
//    @Autowired
//    private AttributeRepository attributeRepository;
//    @Autowired
//    private AttributeTermRepository attributeTermRepository;
//    @Autowired
//    private ProductService productService;
//
//
//    public ProductDashboardController(ProductRepository productRepository, ProductVariationRepository productVariationRepository, CategoriesRepository categoryRepository, StoreRepository storeRepository, AttributeRepository attributeRepository, AttributeTermRepository attributeTermRepository, ProductService productService) {
//        this.productRepository = productRepository;
//        this.productVariationRepository = productVariationRepository;
//        this.categoryRepository = categoryRepository;
//        this.storeRepository = storeRepository;
//        this.attributeRepository = attributeRepository;
//        this.attributeTermRepository = attributeTermRepository;
//        this.productService = productService;
//    }
//
//    @GetMapping("/single/add/{storeId}")
//    public String showAddProductForm(@PathVariable("storeId") Long storeId, Model model) {
//        Product product = new Product();
//        product.setVariations(new ArrayList<>());
//
//        // Lấy danh sách danh mục theo storeId
//        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResourceNotFoundException("store not found"));
//        List<Categories> categories = categoryRepository.findByStore(store);
//        model.addAttribute("idStore", store.getId());
//        model.addAttribute("product", product);
//        model.addAttribute("categories", categories);
//        return "product-dashboard/create-single-product.html";
//    }
//
//
//
//    @PostMapping("/single/add")
//    public String addProduct(
//            @RequestParam("storeId") Long storeId,
//            @RequestParam("files") List<MultipartFile> images,
//            @RequestParam("name") String name,
//            @RequestParam("sku") String sku,
//            @RequestParam("description") String description,
//            @RequestParam("shortDescription") String shortDescription,
//            @RequestParam("price") Double price,
//            @RequestParam(value = "salePrice", required = false) Double salePrice,
//            @RequestParam("categories") Long categoryId,
//            @RequestParam("status") String status
//    ) {
//        Store store = storeRepository.findById(storeId).orElse(null);
//        if (store == null) {
//            return "redirect:/products/add?error=store_not_found";
//        }
//
//        Categories category = categoryRepository.findById(categoryId).orElse(null);
//        if (category == null) {
//            return "redirect:/products/add?error=category_not_found";
//        }
//
//        Product product = new Product();
//        product.setStore(store);
//        product.setName(name);
//        product.setSku(sku);
//        product.setDescription(description);
//        product.setShortDescription(shortDescription);
//        product.setPrice(price);
//        product.setSalePrice(salePrice);
////        product.setStatus(status);
////        product.setCategory(category);
////        product.setIsVariable(false);
//        productRepository.save(product);
//
//        return "redirect:/products/add?success";
//    }
//
//    @GetMapping("/variant/add/{storeId}")
//    public String showAddProductFormVariant(@PathVariable("storeId") Long storeId, Model model) {
//        Product product = new Product();
//        product.setVariations(new ArrayList<>());
//
//        // Lấy danh sách danh mục theo storeId
//        Store store = storeRepository.findById(storeId).orElseThrow(() -> new ResourceNotFoundException("store not found"));
//        List<Categories> categories = categoryRepository.findByStore(store);
//        model.addAttribute("idStore", store.getId());
//        model.addAttribute("product", product);
//        model.addAttribute("categories", categories);
//        return "product-dashboard/create-variant-product.html";
//    }
//
//
//
//
//
//    @PostMapping("/variant/add")
//    public ResponseEntity<String> addProduct(@RequestBody ProductRequest productRequest) {
//        Product savedProduct = productService.saveProduct(productRequest);
//        return ResponseEntity.ok("Sản phẩm " + savedProduct.getName() + " đã được thêm thành công với "
//                + savedProduct.getVariants().size() + " biến thể!");
//    }
//
//
//
//
//
//
//
//}