package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.dto.request.trackby.CreateTrackByRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "track_by")
@Entity(name = "track_by")
public class TrackBy extends BaseEntity {
    @Nationalized
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "map_to", length = 50, nullable = false)
    private String mapTo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TrackBy() {
        super();
    }

    public void set(CreateTrackByRequest request) {
        this.setName(request.getName());
        this.setMapTo(request.getMapTo());
    }
}
