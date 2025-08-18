package cody.ecommerce.cody_app.dto.request.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductImageDTO {
    private String imageUrl;
    private Boolean isMain;
}
