package cody.ecommerce.cody_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackByRepository extends JpaRepository<TrackByRepository, String> {
    // Define any custom query methods if needed
}
