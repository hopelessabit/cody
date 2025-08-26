package cody.ecommerce.cody_app.entity.sub_entity;

import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductIncludedId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_included")
@IdClass(ProductIncludedId.class)
@Getter
@Setter
public class ProductIncluded {
    @Id
    @Column(name = "product_id", length = 50)
    private String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;

    @Id
    @Column(name = "included_product_id", length = 50)
    private String includedProductId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "included_product_id", insertable = false, updatable = false)
    private Product includedProduct;

    public ProductIncluded() {}

    public ProductIncluded(String productId, String includedProductId) {
        this.productId = productId;
        this.includedProductId = includedProductId;
    }

    public static ProductIncluded from(String productId, String includedProductId) {
        return new ProductIncluded(productId, includedProductId);
    }
}