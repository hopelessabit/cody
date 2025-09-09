package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    boolean existsByName(String name);

    Optional<Category> findBySlug(String slug);

    @Query("select (count(c) > 0) from Category c where upper(c.name) = upper(?1) or upper(c.slug) = upper(?2)")
    boolean existsByNameIgnoreCaseOrSlugIgnoreCase(String name, String slug);     // Custom query methods if needed

    Page<Category> findAll(Specification<Category> spec, Pageable pageable);
}