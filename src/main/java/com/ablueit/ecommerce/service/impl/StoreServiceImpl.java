package com.ablueit.ecommerce.service.impl;

import com.ablueit.ecommerce.exception.ResourceNotFoundException;
import com.ablueit.ecommerce.model.Categories;
import com.ablueit.ecommerce.model.Store;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.repository.StoreRepository;
import com.ablueit.ecommerce.repository.UserRepository;
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
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "SELLER-SERVICE")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoreServiceImpl implements StoreService {

    StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    public void deleteListStoreByEntity(List<Store> stores) {
        log.info("deleteListStoreByEntity");

        stores.stream().filter(Store::getEnabled).forEach(x -> x.setEnabled(false));

        log.warn("change all store status to disabled");
        storeRepository.saveAll(stores);
    }

    @Override
    public void deleteStoreById(Long id) {
        log.info("deleteStoreById={}", id);

        Store store = storeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Store not found"));

        log.warn("delete store");
        // just disable store

        store.setEnabled(false);
        storeRepository.save(store);
    }

    @Override
    public Store getStoryById(Long id) {
        log.info("getStoreById={}", id);

        return storeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("store not found"));
    }

    @Override
    public String updateStore(Long id, Store request, Model model) {
        log.info("updateStore={}", id);

        Store store = getStoryById(id);

        checkPermission(store);

        if (Objects.isNull(request.getAddress())) {
            log.warn("address request is null=");
            model.addAttribute("errorMessage", "Address must be not blank");
            return "store-dashboard/update-store";
        }

        if (Objects.isNull(request.getPhone())) {
            log.warn("phone request is null=");
            model.addAttribute("errorMessage", "Phone must be not blank");
            return "store-dashboard/update-store";
        }

        if (Objects.isNull(request.getEmail())) {
            log.warn("email request is null=");
            model.addAttribute("errorMessage", "Email must be not blank");
            return "store-dashboard/update-store";
        }

        if (storeRepository.existsByEmail(request.getEmail())) {
            log.warn("email existed={}", request.getEmail());
            model.addAttribute("errorMessage", "Email existed");
            return "store-dashboard/update-store";
        }

        if (storeRepository.existsByPhone(request.getPhone())) {
            log.warn("phone existed={}", request.getPhone());
            model.addAttribute("errorMessage", "Phone existed");
            return "store-dashboard/update-store";
        }

        store.setEmail(request.getEmail());
        store.setPhone(request.getPhone());
        store.setAddress(request.getAddress());

        log.warn("update store to database");
        storeRepository.save(store);

        model.addAttribute("successMessage", "Update successfully");

        return "store-dashboard/update-store";
    }

  @Override
  public ModelAndView showCategories(Long id, ModelAndView modelAndView) {
    log.info("showCategories store={}", id);

    User owner = getUserFromAuthenticated();

    Store store = getStoryById(id);

    List<Categories> categories = store.getCategories();

    modelAndView.addObject("storeId", id);
    modelAndView.addObject("categories", categories);

    return modelAndView;
  }

  private User getUserFromAuthenticated() {
    log.info("getUserFromAuthenticated");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName(); // Lấy username
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("user not found"));
  }

    private void checkPermission(Store store) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Chuyển đổi danh sách roles thành chuỗi và kiểm tra quyền
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        boolean isOwner = store.getSeller().equals(user);

        if (!isAdmin && !isOwner) {
            throw new RuntimeException("Unauthorized to modify this store");
        }
    }
}
