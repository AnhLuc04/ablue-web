package com.ablueit.ecommerce.service.impl;

import com.ablueit.ecommerce.exception.ResourceNotFoundException;
import com.ablueit.ecommerce.model.Categories;
import com.ablueit.ecommerce.model.Store;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.payload.request.CategoryRequest;
import com.ablueit.ecommerce.repository.CategoriesRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import com.ablueit.ecommerce.service.CategoryService;
import com.ablueit.ecommerce.service.StoreService;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "CATEGORY-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {

    CategoriesRepository categoriesRepository;
    StoreService storeService;
    UserRepository userRepository;

    @Override
    public String create(CategoryRequest request, RedirectAttributes redirectAttributes) {
        log.info("create={}", request.toString());

    // get user for audit
    User seller = getUserFromAuthenticated();
    Store store = storeService.getStoryById(request.storeId());

        if (categoriesRepository.existsByName(request.name())) {
            log.error("category name existed={}", request.name());
            redirectAttributes.addFlashAttribute("errorMessageCategory", "Category name existed");
      return String.format("redirect:/category/"+ store.getId());
        }

        if (categoriesRepository.existsByName(request.name())) {
            log.error("category name existed={}", request.name());
            redirectAttributes.addFlashAttribute("errorMessageCategory", "Category name existed");
      return String.format("redirect:/category/"+ store.getId());
        }

        Categories category = Categories.builder()
                .name(request.name())
                .store(store)
//                .createdBy(seller)
                .build();

        categoriesRepository.save(category);

        redirectAttributes.addFlashAttribute("successMessageCategory", "Created successfully");

    return String.format("redirect:/category/"+ store.getId());
    }

    @Override
    public List<Categories> getCategoriesByStore(Store store) {
        log.info("getCategoriesByStore={}", store.getName());

        return categoriesRepository.findByStore(store);
    }

    @Override
    public String delete(Long id) {
        log.info("delete={}", id);

        Categories category = getCategoriesById(id);

        // get owner store
        Store store = category.getStore();

        log.warn("delete category ={}", id);
        categoriesRepository.delete(category);

    // get owner store

    return String.format("redirect:/category/"+ store.getId());
    }

    private Categories getCategoriesById(Long id) {
        log.info("getCategoriesById={}", id);

        return categoriesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public String edit(Long id, CategoryRequest request, RedirectAttributes redirectAttributes) {
        log.info("edit={} {}", id, request.toString());

        Categories category = getCategoriesById(id);
        Store store = category.getStore();

        if (Objects.isNull(request.name())) {
            log.error("request.name() is null");
            redirectAttributes.addFlashAttribute("errorMessageCategory", "Category name is null");
      return String.format("redirect:/category/"+ store.getId());
        }

        if (categoriesRepository.existsByName(request.name())) {
            log.error("category name existed={}", request.name());
            redirectAttributes.addFlashAttribute("errorEditMessageCategory", "Category name existed");
      return String.format("redirect:/category/"+ store.getId());
        }

        category.setName(request.name());

        log.info("saved category to database");
        categoriesRepository.save(category);
        redirectAttributes.addFlashAttribute("successEditMessageCategory", "Updated successfully");

    return String.format("redirect:/category/"+ store.getId());
    }

    private User getUserFromAuthenticated() {
        log.info("getUserFromAuthenticated");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy username
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user not found"));
    }
}
