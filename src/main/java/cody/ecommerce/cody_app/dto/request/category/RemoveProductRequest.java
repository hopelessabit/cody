package cody.ecommerce.cody_app.dto.request.category;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RemoveProductRequest {
    private Set<String> productIds;
}
