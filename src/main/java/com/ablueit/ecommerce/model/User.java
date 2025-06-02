
package com.ablueit.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends AbstractEntity<Long> implements UserDetails {

    @Column(unique = true, nullable = false)
    String username;

    @Column(unique = true, nullable = false)
    String email;

    @Column(nullable = false)
    String password;


    Boolean enabled = true;

    // Một người dùng có thể có nhiều role
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Một người dùng có thể thuộc một cửa hàng (Nếu là ADMIN thì store = null)
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    // Người tạo tài khoản này
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    // ✅ Lấy danh sách quyền (Authorities) từ danh sách Role
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> (GrantedAuthority) role::getName)
                .collect(Collectors.toSet());
    }
    @OneToMany(mappedBy = "user")
    private List<DiscountCode> discountCodes;

    @Override
    public boolean isAccountNonExpired() {
        return true; // Mặc định tài khoản không hết hạn
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Mặc định tài khoản không bị khóa
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Mật khẩu không hết hạn
    }

    @Override
    public boolean isEnabled() {
        return this.enabled; // Trả về trạng thái enabled của user
    }
}
