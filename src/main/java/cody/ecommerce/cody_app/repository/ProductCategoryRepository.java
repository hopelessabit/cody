package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.relation_entity.ProductCategory;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, ProductCategoryId> {
    // Custom query methods if needed
    long deleteById_ProductIdAndId_CategoryIdIn(String productId, Collection<String> categoryIds);
}