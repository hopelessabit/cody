package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.constant.GradingStatusEnum;
import cody.ecommerce.cody_app.dto.request.task.CreateTaskRequest;
import cody.ecommerce.cody_app.entity.sub_entity.EmployeeTask;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task extends BaseEntity {
    @Nationalized
    @Column(length = 255)
    private String title;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "track_by_id", length = 50)
    private String trackById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "track_by_id", insertable = false, updatable = false)
    private TrackBy trackBy;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "create_by", length = 50)
    private String createById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", insertable = false, updatable = false)
    private User createBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",length = 50)
    private GradingStatusEnum status;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EmployeeTask> employeeTasks;

    public Task() {
        super();
    }

    public Task(CreateTaskRequest request, User createBy) {
        super();
        this.title = request.getTitle();
        this.description = request.getDescription();
        this.trackById = request.getTrackById();
        this.dueDate = request.getDueDate();
        this.createById = createBy.getId();
        this.status = GradingStatusEnum.PROGRESSING;
    }
}
