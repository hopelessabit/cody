package cody.ecommerce.cody_app.entity.sub_entity;

import cody.ecommerce.cody_app.entity.Task;
import cody.ecommerce.cody_app.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "employee_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTask {
    @EmbeddedId
    private EmployeeTaskId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assign_to_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private User assignTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assign_by_id", referencedColumnName = "id", nullable = false)
    private User assignBy;

    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    @Column(name = "evaluation_period", length = 50)
    private String evaluationPeriod;

    public EmployeeTask(String taskId, String assignToId, User assignBy) {
        this.id = new EmployeeTaskId(taskId, assignToId);
        this.setAssignBy(assignBy);
    }

    public static EmployeeTask of(String taskId, String assignToId, User assignBy) {
        return new EmployeeTask(taskId, assignToId, assignBy);
    }

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeTaskId implements Serializable {
        @Column(name = "task_id", nullable = false, length = 50)
        private String taskId;
        @Column(name = "assign_to_id", nullable = false, length = 50)
        private String assignToId;
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EmployeeTaskId that = (EmployeeTaskId) o;
            return Objects.equals(taskId, that.taskId) && Objects.equals(assignToId, that.assignToId);
        }
        @Override
        public int hashCode() {
            return Objects.hash(taskId, assignToId);
        }
    }
}
