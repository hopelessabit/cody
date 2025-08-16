package cody.ecommerce.cody_app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}