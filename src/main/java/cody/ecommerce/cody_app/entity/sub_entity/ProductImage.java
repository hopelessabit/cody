package cody.ecommerce.cody_app.entity.sub_entity;

import cody.ecommerce.cody_app.entity.BaseEntity;
import cody.ecommerce.cody_app.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "product_images")
public class ProductImage extends BaseEntity {
    @Column(name = "product_id", length = 50)
    private String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Product product;

    @Nationalized
    @Column(name = "image_url", length = 700)
    private String imageUrl;

    @Column(name = "is_main")
    private Boolean isMain;

    public ProductImage() {
        super();
    }

    public ProductImage(String id) {
        super(id);
    }
}