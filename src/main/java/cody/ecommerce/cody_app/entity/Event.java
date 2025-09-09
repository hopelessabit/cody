package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.constant.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseEntity {
    @Column(name = "create_by", length = 50)
    private String createById;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", referencedColumnName = "id", insertable = false, updatable = false)
    private User creator;

    @Nationalized
    @Column(name = "title", length = 255)
    private String title;

    @Nationalized
    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name = "slug", length = 255)
    private String slug;

    @Nationalized
    @Column(name = "meta_title", length = 500)
    private String metaTitle;

    @Nationalized
    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Nationalized
    @Column(name = "location", length = 500)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private EventStatus status;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

