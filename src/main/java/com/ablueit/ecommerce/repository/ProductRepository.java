package com.ablueit.ecommerce.repository;

import com.ablueit.ecommerce.model.Product;
import com.ablueit.ecommerce.payload.request.VariantRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findTop8ByOrderBySalesCountDesc(); // 👈 Top 8 sản phẩm bán chạy
    Optional<Product> findById(Long aLong);
    Page<Product> findByStoreId(Long storeId, Pageable pageable);
    Optional<Product> findBySku(String sku);

    Page<Product> findByPriceBetween(int min, int max, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.name = :categoryName AND p.price BETWEEN :min AND :max")
    Page<Product> findByCategoryNameAndPriceBetween(
            @Param("categoryName") String categoryName,
            @Param("min") Double min,
            @Param("max") Double max,
            Pageable pageable
    );

    Page<Product> findByPriceBetween(Double min, Double max, Pageable pageable);

    @Query(value = "SELECT p.* FROM product p " +
            "LEFT JOIN product_category pc ON pc.product_id = p.product_id " +
            "WHERE pc.category_id = :categoryId " +
            "AND p.product_id != :currentProductId " +
            "LIMIT :limit",
            nativeQuery = true)
    List<Product> findRelatedProductsByCategoryId(@Param("categoryId") Long categoryId,
                                                  @Param("currentProductId") Long currentProductId,
                                                  @Param("limit") int limit);


    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN p.categories c " +
            "WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:category IS NULL OR LOWER(c.name) = LOWER(:category)) " +
            "AND (:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("category") String category,
                                 @Param("maxPrice") Double maxPrice,
                                 Pageable pageable);

}
