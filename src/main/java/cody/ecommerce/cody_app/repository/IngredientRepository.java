package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, String> {
    // Custom query methods if needed
}