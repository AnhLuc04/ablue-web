package com.ablueit.ecommerce;

import com.ablueit.ecommerce.model.Role;
import com.ablueit.ecommerce.model.User;
import com.ablueit.ecommerce.repository.RoleRepository;
import com.ablueit.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@SpringBootApplication
public class AblueItApplication {

    public static void main(String[] args) {
        SpringApplication.run(AblueItApplication.class, args);
    }

@Bean
CommandLineRunner init(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
    return args -> {
        // Kiểm tra xem ROLE_ADMIN đã tồn tại chưa
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName("ROLE_ADMIN");
            return roleRepository.save(newRole); // Lưu Role vào DB trước
        });

        // Kiểm tra xem user "admin" đã tồn tại chưa
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRoles(Collections.singleton(adminRole)); // Gán role đã lưu);
            userRepository.save(admin);
            System.out.println("✅ Tài khoản ADMIN đã được tạo thành công!");
        } else {
            System.out.println("⚠️ Tài khoản ADMIN đã tồn tại, không cần tạo lại.");
        }
    };
}


}
