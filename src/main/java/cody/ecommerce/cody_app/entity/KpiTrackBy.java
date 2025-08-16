package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.entity.sub_entity_id.KpiTrackById;
import jakarta.persistence.*;

@Entity
@Table(name = "kpi_track_by")
@IdClass(KpiTrackById.class)
public class KpiTrackBy {
    @Id
    @Column(name = "kpi_id", length = 50)
    private String kpiId;

    @Id
    @Column(name = "track_by_id", length = 50)
    private String trackById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Kpi kpi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    private TrackBy trackBy;

    public KpiTrackBy() {}

    public KpiTrackBy(String kpiId, String trackById) {
        this.kpiId = kpiId;
        this.trackById = trackById;
    }

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public String getTrackById() {
        return trackById;
    }

    public void setTrackById(String trackById) {
        this.trackById = trackById;
    }

    public Kpi getKpi() {
        return kpi;
    }

    public void setKpi(Kpi kpi) {
        this.kpi = kpi;
    }

    public TrackBy getTrackBy() {
        return trackBy;
    }

    public void setTrackBy(TrackBy trackBy) {
        this.trackBy = trackBy;
    }
}