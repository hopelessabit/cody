package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.entity.sub_entity.ProductIngredient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ingredients")
public class Ingredient extends BaseEntity {
    @Nationalized
    @Column(name = "name", length = 255)
    private String name;

    @OneToMany(mappedBy = "ingredient", fetch = FetchType.LAZY)
    private Set<ProductIngredient> productIngredients = new java.util.HashSet<>();

    public Ingredient() {
        super();
    }

    public Ingredient(String id) {
        super(id);
    }
}