package cody.ecommerce.cody_app.entity;

import cody.ecommerce.cody_app.util.IdUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseEntity {
    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    public BaseEntity(String id) {
        this.id = id;
    }

    public BaseEntity() {
        this.id = IdUtil.generateId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
