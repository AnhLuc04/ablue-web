package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.model.Categories;
import com.ablueit.ecommerce.model.Store;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.payload.request.CategoryRequest;
import com.ablueit.ecommerce.repository.CategoriesRepository;
import com.ablueit.ecommerce.repository.StoreRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import com.ablueit.ecommerce.service.CategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
@Slf4j(topic = "CATEGORY-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {

    @Autowired
    UserRepository userRepository;
    CategoryService categoryService;
    CategoriesRepository categoriesRepository;
    StoreRepository storeRepository;
    @GetMapping("/{id}")
    public ModelAndView showDetailDashboard(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("store-dashboard/dashboard-store");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            modelAndView.setViewName("error/403");
            return modelAndView;
        }

        Store store = userOptional.get().getStore();
        List<Categories> categories = categoryService.getCategoriesByStore(store);

        modelAndView.addObject("storeId", id);
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @PostMapping("/")
    public ResponseEntity<Categories> createCategory(@RequestBody CategoryRequest request) {
        // Tìm cửa hàng theo ID
        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy cửa hàng với ID: " + request.storeId()));

        // Tạo danh mục mới
        Categories category = new Categories();
        category.setName(request.name());
        category.setStore(store);

        // Lưu vào DB
        Categories savedCategory = categoriesRepository.save(category);

        // Trả về JSON
        return ResponseEntity.ok(savedCategory);
    }



    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id){
        log.info("POST /category/delete/{}", id);


        return categoryService.delete(id);
    }

    @PostMapping("/edit/{id}")
    public String edit(@PathVariable Long id, @ModelAttribute CategoryRequest request, RedirectAttributes redirectAttributes) {
        log.info("POST /category/edit/{}", id);

        return categoryService.edit(id, request, redirectAttributes);
    }

}
