package cody.ecommerce.cody_app.entity.sub_entity_id;

import java.io.Serializable;
import java.util.Objects;

public class KpiTrackById implements Serializable {
    private String kpiId;
    private String trackById;

    public KpiTrackById() {}

    public KpiTrackById(String kpiId, String trackById) {
        this.kpiId = kpiId;
        this.trackById = trackById;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KpiTrackById)) return false;
        KpiTrackById that = (KpiTrackById) o;
        return Objects.equals(kpiId, that.kpiId) &&
                Objects.equals(trackById, that.trackById);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kpiId, trackById);
    }
}