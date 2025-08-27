package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product>{
    boolean existsBySlug(String slug);

    boolean existsByMetaDescription(String metaDescription);

    Optional<Product> findBySlug(String slug);

    Optional<Product> findByIdAndIsHidden(String id, Boolean isHidden);

    Optional<Product> findBySlugAndIsHidden(String slug, Boolean isHidden);

    Optional<Product> findByNameContainsIgnoreCase(String name);

    List<Product> findAllByNameContainsIgnoreCase(String productName);

    @Query("SELECT DISTINCT p FROM products p " +
            "LEFT JOIN p.categories c " +
            "WHERE p.isHidden = false AND p.stockQuantity > 0 AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT DISTINCT p FROM products p " +
            "LEFT JOIN p.categories c " +
            "WHERE p.isHidden = false AND p.stockQuantity > 0 AND " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Product> findByCategory(@Param("category") String category);

    @Query("SELECT DISTINCT p FROM products p " +
            "WHERE p.isHidden = false AND p.stockQuantity > 0 AND " +
            "p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
}
