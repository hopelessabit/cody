package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    // Define any custom query methods if needed
}
