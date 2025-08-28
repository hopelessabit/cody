package cody.ecommerce.cody_app.entity.sub_entity_id;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class ProductIncludedId implements Serializable {
    private String productId;
    private String includedProductId;

    public ProductIncludedId() {}

    public ProductIncludedId(String productId, String includedProductId) {
        this.productId = productId;
        this.includedProductId = includedProductId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductIncludedId)) return false;
        ProductIncludedId that = (ProductIncludedId) o;
        return Objects.equals(productId, that.productId) &&
                Objects.equals(includedProductId, that.includedProductId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, includedProductId);
    }
}
