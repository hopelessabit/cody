package cody.ecommerce.cody_app.dto.request.order;

import cody.ecommerce.cody_app.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderAddressRequest {
    private String addressUrl;

    public void validate() {
        if (addressUrl == null || addressUrl.isEmpty()) {
            throw new BadRequestException("Address URL cannot be empty.");
        }
        addressUrl = addressUrl.trim();
    }
}
