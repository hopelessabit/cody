package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.constant.KpiStatus;
import cody.ecommerce.cody_app.dto.request.kpi.CreateKpiRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "kpis")
public class Kpi extends BaseEntity {
    @Nationalized
    @Column(length = 255)
    private String title;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @CreationTimestamp
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate;

    @Column(name = "create_by", length = 50)
    private String createById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", insertable = false, updatable = false)
    private User createBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private KpiStatus status;

    @Column(name = "assign_to_id", length = 50)
    private String assignToId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assign_to_id", insertable = false, updatable = false)
    private User assignTo;

    @Column(name = "input_target_value")
    private Integer inputTargetValue;

    @Column(name = "input_current_progress")
    private Integer inputCurrentProgress;

    @Column(name = "selled_target_value")
    private Integer selledTargetValue;

    @Column(name = "selled_current_progress")
    private Integer selledCurrentProgress;

    public Kpi() {
        super();
    }

    public Kpi(CreateKpiRequest request, User createBy) {
        super();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.dueDate = request.getDueDate();
        this.createById = createBy.getId();
        this.assignToId = request.getAssignToId();
        this.inputTargetValue = request.getInputTargetValue() != null ? request.getInputTargetValue() : 0;
        this.inputCurrentProgress = request.getInputCurrentProgress() != null ? request.getInputCurrentProgress() : 0;
        this.selledTargetValue = request.getSelledTargetValue() != null ? request.getSelledTargetValue() : 0;
        this.selledCurrentProgress = request.getSelledCurrentProgress() != null ? request.getSelledCurrentProgress() : 0;
        this.status = KpiStatus.INCOMPLETE;
    }

    /**
     * Updates KPI status based on current progress vs targets
     */
    public void updateStatus() {
        boolean inputComplete = this.inputTargetValue <= this.inputCurrentProgress;
        boolean selledComplete = this.selledTargetValue <= this.selledCurrentProgress;

        if (inputComplete && selledComplete) {
            this.status = KpiStatus.COMPLETE;
        } else {
            this.status = KpiStatus.INCOMPLETE;
        }
    }
}
