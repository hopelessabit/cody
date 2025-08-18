package cody.ecommerce.cody_app.dto.request.product;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddImageToProductDTO {
    private String imageUrl;
    private Boolean isMain;
}