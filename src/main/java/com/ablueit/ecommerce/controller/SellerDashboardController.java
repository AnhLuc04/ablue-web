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

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/seller")
@PreAuthorize("hasRole('ROLE_SELLER')")
@Slf4j(topic = "SELLER-DASHBOARD-CONTROLLER")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SellerDashboardController {

    StoreRepository storeRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    UserRepository userRepository;
    UserRepository userService;
    SellerService sellerService;

    @GetMapping("/dashboard")
    public String sellerDashboard(Model model) {

        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        List<User> sellers = userService.findUsersCreatedBySeller(username)
                .stream().filter(User::isEnabled).toList(); // Lấy danh sách nhân viên (role SELLER)
        User seller = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        List<Store> websites = storeRepository.findStoresByUserId(seller.getId());

        List<Store> websitesEnabled = websites.stream().filter(Store::getEnabled).toList();

        model.addAttribute("role", "Seller");
        model.addAttribute("sellers", sellers);
        model.addAttribute("websites", websitesEnabled);

        return "seller-dashboard/seller";
    }


    @GetMapping("/create-user")
    public ModelAndView showCreateSellerForm() {
        ModelAndView modelAndView = new ModelAndView("seller-dashboard/create-user");
        modelAndView.addObject("user", new User());
        return modelAndView;
    }

    @PostMapping("/create-user")
    public ModelAndView createSeller(@ModelAttribute("user") User user) {
        ModelAndView modelAndView = new ModelAndView("seller-dashboard/create-user");

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
        Role sellerRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_USER");
                    return roleRepository.save(newRole);
                });

        seller.getRoles().add(sellerRole);  // Gán Role SELLER cho User

        userRepository.save(seller);  // Lưu Seller vào cơ sở dữ liệu

        modelAndView.addObject("successMessage", "Tạo tài khoản SELLER thành công!");
        return modelAndView;
    }

    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {
        log.info("GET /profile");

        return sellerService.getDetails(model, principal);
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User seller, Principal principal, Model model) {
        log.info("POST /update");

        return sellerService.updateProfile(seller, principal, model);
    }

    @PostMapping("/delete/{id}")
    public String deleteStaff(@PathVariable Long id) {
        log.info("POST /delete/{}", id);

        sellerService.deleteStaff(id);

        return "redirect:/seller/dashboard";
    }

}
