package com.ablueit.ecommerce.model;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

import jakarta.persistence.Entity;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code; // Ví dụ: template1
    private String home; // Giao diện đẹp cho user
    private String description;

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Có thể thêm trường previewImage nếu muốn

    // Constructors, Getters, Setters
}
