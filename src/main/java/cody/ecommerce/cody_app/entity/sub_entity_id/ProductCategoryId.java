package cody.ecommerce.cody_app.entity.sub_entity_id;

import java.io.Serializable;
import java.util.Objects;

public class ProductCategoryId implements Serializable {
    private String productId;
    private String categoryId;

    public ProductCategoryId() {}

    public ProductCategoryId(String productId, String categoryId) {
        this.productId = productId;
        this.categoryId = categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategoryId that)) return false;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, categoryId);
    }
}