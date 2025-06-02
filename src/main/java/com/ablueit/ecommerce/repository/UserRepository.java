package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    // Tìm tất cả user do SELLER tạo
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_USER' AND u.createdBy.username = :currentUsername")
    List<User> findUsersCreatedBySeller(@Param("currentUsername") String currentUsername);
    ;
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_SELLER' AND u.createdBy.username = :adminUsername")
    List<User> findSellersCreatedByAdmin(@Param("adminUsername") String adminUsername);

    boolean existsByEmail(String email);

    Optional<User> findUserById(Long id);
}
