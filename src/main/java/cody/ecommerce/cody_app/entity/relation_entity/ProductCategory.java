package cody.ecommerce.cody_app.entity.relation_entity;

import cody.ecommerce.cody_app.entity.Category;
import cody.ecommerce.cody_app.entity.Product;
import jakarta.persistence.*;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductCategoryId;

@Entity
@Table(name = "product_categories")
@IdClass(ProductCategoryId.class)
public class ProductCategory {
    @Id
    @Column(name = "product_id", length = 50)
    private String productId;

    @Id
    @Column(name = "category_id", length = 50)
    private String categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Category category;

    public ProductCategory() {}

    public ProductCategory(String productId, String categoryId) {
        this.productId = productId;
        this.categoryId = categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

}