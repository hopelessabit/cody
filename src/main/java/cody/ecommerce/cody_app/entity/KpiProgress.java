package cody.ecommerce.cody_app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "kpi_progress")
public class KpiProgress extends BaseEntity {
    @Column(name = "kpi_id", length = 50)
    private String kpiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Kpi kpi;

    @Column(name = "employee_kpi_id", length = 50)
    private String employeeKpiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_kpi_id", referencedColumnName = "id", insertable = false, updatable = false)
    private EmployeeKpi employeeKpi;

    @Column(name = "track_by_id", length = 50)
    private String trackById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_by_id", referencedColumnName = "id", insertable = false, updatable = false)
    private TrackBy trackBy;

    @Nationalized
    @Column(name = "result", length = 50)
    private String result;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    public KpiProgress() {
        super();
    }

    public KpiProgress(String id) {
        super(id);
    }

    // Getters and setters

    public String getKpiId() {
        return kpiId;
    }

    public void setKpiId(String kpiId) {
        this.kpiId = kpiId;
    }

    public Kpi getKpi() {
        return kpi;
    }

    public void setKpi(Kpi kpi) {
        this.kpi = kpi;
    }

    public String getEmployeeKpiId() {
        return employeeKpiId;
    }

    public void setEmployeeKpiId(String employeeKpiId) {
        this.employeeKpiId = employeeKpiId;
    }

    public EmployeeKpi getEmployeeKpi() {
        return employeeKpi;
    }

    public void setEmployeeKpi(EmployeeKpi employeeKpi) {
        this.employeeKpi = employeeKpi;
    }

    public String getTrackById() {
        return trackById;
    }

    public void setTrackById(String trackById) {
        this.trackById = trackById;
    }

    public TrackBy getTrackBy() {
        return trackBy;
    }

    public void setTrackBy(TrackBy trackBy) {
        this.trackBy = trackBy;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}