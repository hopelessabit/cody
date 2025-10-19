package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Ingredient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, String> {
    // Custom query methods if needed

    @Query("select (count(i) > 0) from Ingredient i where upper(i.name) = upper(?1)")
    boolean existsByNameIgnoreCase(String name);

    @Query("select (count(i) > 0) from Ingredient i where i.id != ?2 and upper(i.name) = upper(?1)")
    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);

    Page<Ingredient> findAll(Specification<Ingredient> spec, Pageable pageable);
}