package cody.ecommerce.cody_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Category extends BaseEntity {
    @Nationalized
    @Column(name = "name", length = 100)
    private String name;

    @Nationalized
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "slug", length = 255, nullable = false, unique = true)
    private String slug;

    @Nationalized
    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Category() {
        super();
    }

    public Category(String id) {
        super(id);
    }

}