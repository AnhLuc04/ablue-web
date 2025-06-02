package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("SELECT s FROM Store s WHERE s.seller.id = :userId")
    List<Store> findStoresByUserId(@Param("userId") Long userId);
    @Query("SELECT s FROM Store s WHERE s.seller IN (SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ROLE_SELLER' AND u.createdBy.username = :adminUsername)")
    List<Store> findStoresBySellersCreatedByAdmin(@Param("adminUsername") String adminUsername);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
//    Optional<Store> findByIdAndUser(Long id, User user);
}
