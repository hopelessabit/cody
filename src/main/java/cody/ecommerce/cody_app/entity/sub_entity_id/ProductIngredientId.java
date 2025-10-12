package cody.ecommerce.cody_app.entity.sub_entity_id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductIngredientId implements Serializable {
    private String productId;
    private String ingredientId;
}