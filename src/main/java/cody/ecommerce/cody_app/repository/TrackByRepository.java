package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.TrackBy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackByRepository extends JpaRepository<TrackBy, String> {
}

