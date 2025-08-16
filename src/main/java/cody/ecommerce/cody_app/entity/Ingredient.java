package cody.ecommerce.cody_app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@Entity
@Table(name = "ingredients")
public class Ingredient extends BaseEntity {
    @Nationalized
    @Column(name = "name", length = 255)
    private String name;

    public Ingredient() {
        super();
    }

    public Ingredient(String id) {
        super(id);
    }
}