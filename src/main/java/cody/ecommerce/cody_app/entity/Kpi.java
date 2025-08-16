package cody.ecommerce.cody_app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;

import java.time.LocalDateTime;

@Entity
@Table(name = "kpis")
public class Kpi extends BaseEntity {
    @Nationalized
    @Column(name = "name", length = 255)
    private String name;

    @Nationalized
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "weight")
    private Integer weight;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "create_by", length = 50)
    private String createBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User createByUser;

    public Kpi() {
        super();
    }

    public Kpi(String id) {
        super(id);
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public User getCreateByUser() {
        return createByUser;
    }

    public void setCreateByUser(User createByUser) {
        this.createByUser = createByUser;
    }
}