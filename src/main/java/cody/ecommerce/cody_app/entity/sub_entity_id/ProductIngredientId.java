package cody.ecommerce.cody_app.entity.sub_entity_id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ProductIngredientId implements Serializable {
    @Column(name = "product_id", length = 50)
    private String productId;

    @Column(name = "ingredient_id", length = 50)
    private String ingredientId;

}