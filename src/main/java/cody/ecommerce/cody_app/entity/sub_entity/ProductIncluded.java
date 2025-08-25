package cody.ecommerce.cody_app.entity.sub_entity;

import cody.ecommerce.cody_app.entity.sub_entity_id.ProductIncludedId;
import jakarta.persistence.*;

@Entity
@Table(name = "product_included")
@IdClass(ProductIncludedId.class)
public class ProductIncluded {
    @Id
    @Column(name = "product_id", length = 50)
    private String productId;

    @Id
    @Column(name = "included_product_id", length = 50)
    private String includedProductId;

    public ProductIncluded() {}

    public ProductIncluded(String productId, String includedProductId) {
        this.productId = productId;
        this.includedProductId = includedProductId;
    }

    public static ProductIncluded from(String productId, String includedProductId) {
        return new ProductIncluded(productId, includedProductId);
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getIncludedProductId() {
        return includedProductId;
    }

    public void setIncludedProductId(String includedProductId) {
        this.includedProductId = includedProductId;
    }
}