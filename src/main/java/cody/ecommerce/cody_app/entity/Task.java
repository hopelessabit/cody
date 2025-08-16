package cody.ecommerce.cody_app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Nationalized
    @Column(name = "title", length = 255)
    private String title;

    @Nationalized
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "create_by", length = 50)
    private String createBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "create_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User createByUser;

    @Nationalized
    @Column(name = "status", length = 50)
    private String status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedBy() {
        return createBy;
    }

    public void setCreatedBy(String createBy) {
        this.createBy = createBy;
    }

    public User getCreateByUser() {
        return createByUser;
    }

    public void setCreateByUser(User createByUser) {
        this.createByUser = createByUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
