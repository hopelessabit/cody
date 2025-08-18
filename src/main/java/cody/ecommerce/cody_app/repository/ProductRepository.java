package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsBySlug(String slug);

    boolean existsByMetaDescription(String metaDescription);
}
