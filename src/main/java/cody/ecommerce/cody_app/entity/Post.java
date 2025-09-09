package cody.ecommerce.cody_app.entity;

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
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {
    @Column(name = "author_id", length = 50)
    private String authorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User author;

    @Nationalized
    @Column(name = "title", length = 255)
    private String title;

    @Nationalized
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "slug", length = 255)
    private String slug;

    @Column(name = "meta_title", length = 255)
    private String metaTitle;

    @Nationalized
    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Nationalized
    @Column(name = "content", columnDefinition = "NVARCHAR(MAX)")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 100)
    private cody.ecommerce.cody_app.constant.PostType type;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
