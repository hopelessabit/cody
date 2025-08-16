package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Kpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KpiRepository extends JpaRepository<Kpi, String> {
    // Custom query methods if needed
}