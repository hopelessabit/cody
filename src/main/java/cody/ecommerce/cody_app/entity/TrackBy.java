package cody.ecommerce.cody_app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "track_by")
public class TrackBy extends BaseEntity{

    @Nationalized
    @Column(name = "name", length = 100)
    private String name;

    @Nationalized
    @Column(name = "map_to", length = 50)
    private String mapTo;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMapTo() {
        return mapTo;
    }

    public void setMapTo(String mapTo) {
        this.mapTo = mapTo;
    }

    public TrackBy() {
        super();
    }
}