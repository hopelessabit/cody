package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.sub_entity.EmployeeTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeTaskRepository extends JpaRepository<EmployeeTask, EmployeeTask.EmployeeTaskId> {
}

