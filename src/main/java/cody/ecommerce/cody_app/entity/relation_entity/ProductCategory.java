package cody.ecommerce.cody_app.entity.relation_entity;

import cody.ecommerce.cody_app.entity.Category;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductCategoryId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "product_categories")
public class ProductCategory {
    @EmbeddedId
    private ProductCategoryId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Category category;

    public ProductCategory() {}

    public ProductCategory(String productId, String categoryId) {
        this.id = new ProductCategoryId(productId, categoryId);
    }

    public static ProductCategory of(String productId, String categoryId) {
        return new ProductCategory(productId, categoryId);
    }
}