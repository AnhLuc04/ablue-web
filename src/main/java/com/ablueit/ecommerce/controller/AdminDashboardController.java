package com.ablueit.ecommerce.controller;

import com.ablueit.ecommerce.model.Role;
import com.ablueit.ecommerce.model.Store;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.repository.RoleRepository;
import com.ablueit.ecommerce.repository.StoreRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import com.ablueit.ecommerce.service.SellerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Slf4j(topic = "ADMIN-DASHBOARD-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminDashboardController {

    StoreRepository storeRepository;
    RoleRepository roleRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    SellerService sellerService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("GET /dashboard");

        log.info("current user={} {}", userDetails.getUsername(), userDetails.getAuthorities());

        // Lấy danh sách user có vai trò ROLE_SELLER
        List<User> sellerUsers = userRepository.findSellersCreatedByAdmin(userDetails.getUsername());

        // Lấy danh sách store do seller tạo
        List<Store> sellerStores = storeRepository.findStoresBySellersCreatedByAdmin(userDetails.getUsername());

        // get sellers have enable status
//        List<User> sellerEnableStatus = sellerUsers.stream().filter(User::getEnabled).toList();

        model.addAttribute("role", "Admin");
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("sellers", sellerUsers);
        model.addAttribute("stores", sellerStores);

        return "admin-dashboard/admin"; // Trả về template Thymeleaf
    }

    @GetMapping("/create-seller")
    public ModelAndView showCreateSellerForm() {
        log.info("GET /create-seller");

        ModelAndView modelAndView = new ModelAndView("admin-dashboard/create-seller");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping("/create-seller")
    public ModelAndView createSeller(@ModelAttribute("user") User user) {
        log.info("POST /create-seller");

        ModelAndView modelAndView = new ModelAndView("admin-dashboard/create-seller");

        if (userRepository.existsByUsername(user.getUsername())) {
            modelAndView.addObject("errorMessage", "Tài khoản đã tồn tại!");
            return modelAndView;
        }

        // Tạo và gán secret_key

        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        User createby = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found"));


        // Tạo mới Seller
        User seller = new User();
        seller.setUsername(user.getUsername());
        seller.setPassword(passwordEncoder.encode(user.getPassword()));  // Mã hóa mật khẩu
        seller.setCreatedBy(createby);

        // Nếu email không được cung cấp, gán email mặc định
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            seller.setEmail(user.getUsername() + "@gmail.com");  // Gán email mặc định
        } else {
            seller.setEmail(user.getEmail());  // Gán email từ form
        }

        // Tạo Role SELLER và gán vào User
        Role sellerRole = roleRepository.findByName("ROLE_SELLER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_SELLER");
                    return roleRepository.save(newRole);
                });

        seller.getRoles().add(sellerRole);  // Gán Role SELLER cho User

        userRepository.save(seller);  // Lưu Seller vào cơ sở dữ liệu

        modelAndView.addObject("successMessage", "Tạo tài khoản SELLER thành công!");
        return modelAndView;
    }

    @PostMapping("/delete-seller/{id}")
    public String deleteSeller(@PathVariable Long id) {
        log.info("POST /delete/{}", id);

        sellerService.deleteStaff(id);

        return "redirect:/admin/dashboard";
    }

}
