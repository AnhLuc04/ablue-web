package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.model.Categories;
import com.ablueit.ecommerce.model.Store;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.repository.StoreRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import com.ablueit.ecommerce.service.CategoryService;
import com.ablueit.ecommerce.service.StoreService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;


@Controller
@RequiredArgsConstructor
@RequestMapping("/store/dashboard")
@PreAuthorize("hasAnyRole('ROLE_SELLER')")
@Slf4j(topic = "STORE-DASHBOARD_CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StoreDashboardController {

    StoreRepository storeRepository;
    UserRepository userRepository;
    StoreService storeService;
    CategoryService categoryService;


    @GetMapping("/{id}")
    public ModelAndView showDetailDashboard(@PathVariable("id") Long id) {
        ModelAndView modelAndView = new ModelAndView("store-dashboard/dashboard-store");

        // 🔥 Lấy thông tin tài khoản đang đăng nhập
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy username
        Optional<User> userOptional = userRepository.findByUsername(username);

        if (userOptional.isEmpty()) {
            modelAndView.setViewName("error/403"); // Không có quyền truy cập
            return modelAndView;
        }

        Store store = userOptional.get().getStore();

        List<Categories> categories = categoryService.getCategoriesByStore(store);

        modelAndView.addObject("storeId", id);
        modelAndView.addObject("categories", categories);

        return modelAndView;
    }


    @GetMapping("/detail/{id}")
    public ModelAndView showDetail(@PathVariable("id") Long id) {
        log.info("GET /detail/{}", id);

        ModelAndView modelAndView = new ModelAndView("store-dashboard/detail-store");

        storeRepository.findById(id).ifPresentOrElse(
                value -> modelAndView.addObject("store", value),
                () -> modelAndView.setViewName("error/404")
        );

        return modelAndView;
    }


    @GetMapping("/create-store")
    public String showCreateStoreForm(Model model) {
        model.addAttribute("store", new Store());
        return "store-dashboard/create-store";
    }

    @PostMapping("/create-store")
    public String createStore(@ModelAttribute("store") Store store, Model model) {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        // Kiểm tra nếu cửa hàng đã tồn tại
        if (storeRepository.existsByName(store.getName()) || storeRepository.existsByEmail(store.getEmail())) {
            model.addAttribute("errorMessage", "Tên cửa hàng hoặc email đã tồn tại!");
            return "store-dashboard/create-store";
        }
        // store.setDateTime(LocalDateTime.now());
        store.setSeller(seller);
        storeRepository.save(store);

        seller.setStore(store);
        userRepository.save(seller);

        model.addAttribute("successMessage", "Cửa hàng đã được tạo thành công!");
        return "store-dashboard/create-store";

    }


    // 4️⃣ Hiển thị form chỉnh sửa Store
    @GetMapping("/update/{id}")
    public String showEditStoreForm(@PathVariable Long id, Model model) {
        log.info("GET /update/{}", id);

        Store store = storeService.getStoryById(id);
        log.info("store name={}", store.getName());

        model.addAttribute("store", store);

        return "store-dashboard/update-store";
    }

    // 5️⃣ Cập nhật Store (Admin hoặc chủ sở hữu)
    @PostMapping("/update/{id}")
    public String updateStore(@PathVariable Long id, @ModelAttribute Store request, Model model) {
        log.info("POST /update/{}", id);

        return storeService.updateStore(id, request, model);
    }

    // 6️⃣ Xóa Store
    @PostMapping("/delete/{id}")
    public String deleteStore(@PathVariable Long id) {
        log.info("POST /delete/{}", id);

        storeService.deleteStoreById(id);

        return "redirect:/seller/dashboard";
    }



}
