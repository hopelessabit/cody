package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.KpiProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KpiProgressRepository extends JpaRepository<KpiProgress, String> {
    // Custom query methods if needed
}