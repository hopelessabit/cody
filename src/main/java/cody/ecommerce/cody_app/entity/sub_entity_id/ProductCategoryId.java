package cody.ecommerce.cody_app.entity.sub_entity_id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategoryId implements Serializable {
    @Column(name = "product_id", length = 50)
    private String productId;

    @Column(name = "category_id", length = 50)
    private String categoryId;

    public static ProductCategoryId of(String productId, String categoryId) {
        return new ProductCategoryId(productId, categoryId);
    }
}