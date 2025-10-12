package cody.ecommerce.cody_app.entity.sub_entity;

import cody.ecommerce.cody_app.entity.Ingredient;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.sub_entity_id.ProductIngredientId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_ingredients")
public class ProductIngredient {
    @EmbeddedId
    private ProductIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
}


