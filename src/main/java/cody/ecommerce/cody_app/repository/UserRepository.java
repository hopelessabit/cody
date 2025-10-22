package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findFirstByEmail(String email);

    User findFirstById(String id);

    boolean existsByEmail(String email);

    boolean existsById(String id);

    Page<User> findAll(Specification<User> spec, Pageable pageable);
}
