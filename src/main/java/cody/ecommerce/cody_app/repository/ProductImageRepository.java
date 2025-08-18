package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.sub_entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, String> {
    List<ProductImage> findAllByProductId(String productId);
    // Custom query methods if needed
}