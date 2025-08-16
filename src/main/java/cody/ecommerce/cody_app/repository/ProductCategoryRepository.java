package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.relation_entity.ProductCategory;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {
    // Custom query methods if needed
}