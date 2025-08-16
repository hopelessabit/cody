package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.EmployeeKpi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeKpiRepository extends JpaRepository<EmployeeKpi, String> {
    // Custom query methods if needed
}