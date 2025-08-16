package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    // Custom query methods if needed
}