package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.sub_entity.ProductIncluded;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductIncludedId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductIncludedRepository extends JpaRepository<ProductIncluded, ProductIncludedId> {
    // Custom query methods if needed
}