package cody.ecommerce.cody_app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "employee_kpis")
public class EmployeeKpi extends BaseEntity {
    @Column(name = "kpi_id", length = 50)
    private String kpiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kpi_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Kpi kpi;

    @Column(name = "assign_to", length = 50)
    private String assignTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assign_to", referencedColumnName = "id", insertable = false, updatable = false)
    private User assignToUser;

    @Column(name = "assign_by", length = 50)
    private String assignBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assign_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User assignByUser;

    @Column(name = "score", precision = 5, scale = 2)
    private Double score;

    @Nationalized
    @Column(name = "evaluation_period", length = 50)
    private String evaluationPeriod;

    public EmployeeKpi() {
        super();
    }

    public EmployeeKpi(String id) {
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

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public User getAssignToUser() {
        return assignToUser;
    }

    public void setAssignToUser(User assignToUser) {
        this.assignToUser = assignToUser;
    }

    public String getAssignBy() {
        return assignBy;
    }

    public void setAssignBy(String assignBy) {
        this.assignBy = assignBy;
    }

    public User getAssignByUser() {
        return assignByUser;
    }

    public void setAssignByUser(User assignByUser) {
        this.assignByUser = assignByUser;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getEvaluationPeriod() {
        return evaluationPeriod;
    }

    public void setEvaluationPeriod(String evaluationPeriod) {
        this.evaluationPeriod = evaluationPeriod;
    }
}