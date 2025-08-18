package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String>, JpaSpecificationExecutor<Product>{
    boolean existsBySlug(String slug);

    boolean existsByMetaDescription(String metaDescription);

    Optional<Product> findBySlug(String slug);

    Optional<Product> findByIdAndIsHidden(String id, Boolean isHidden);

    Optional<Product> findBySlugAndIsHidden(String slug, Boolean isHidden);
}
