package cody.ecommerce.cody_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "products")
public class Product extends BaseEntity{
    @Nationalized
    @Column(length = 255)
    private String name;

    @Nationalized
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(length = 255)
    private String slug;

    @Nationalized
    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", precision = 18, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}