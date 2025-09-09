package cody.ecommerce.cody_app.dto.request.product;

import cody.ecommerce.cody_app.constant.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductIncludedRequest {
    private String productIncludedId;
    private Action action;
}
