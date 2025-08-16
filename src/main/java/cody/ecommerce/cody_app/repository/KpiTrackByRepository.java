package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.entity.KpiTrackBy;
import cody.ecommerce.cody_app.entity.sub_entity_id.KpiTrackById;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KpiTrackByRepository extends JpaRepository<KpiTrackBy, KpiTrackById> {
    // Custom query methods if needed
}