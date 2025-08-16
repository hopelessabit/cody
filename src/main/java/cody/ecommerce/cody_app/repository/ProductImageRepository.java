package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.sub_entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    // Custom query methods if needed
}