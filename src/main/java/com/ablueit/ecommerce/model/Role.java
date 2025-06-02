package com.ablueit.ecommerce.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor

public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public Role(String role_admin) {
        this.name=role_admin;
    }

    public Role(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
