package cody.ecommerce.cody_app.dto.request.order;

import cody.ecommerce.cody_app.constant.PaymentMethodEnum;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private List<CreateOrderItemRequest> items;
    private String buyerName;
    private String buyerPhone;
    private String addressUrl;
    private String note;
    private PaymentMethodEnum paymentMethod;
    private String sellerId;

    public void validate() {
        Map<String, String> errors = new HashMap<>();
        if (items == null || items.isEmpty()) {
            errors.put("items", "Order items cannot be empty.");
        }
        if (buyerPhone == null || buyerPhone.isEmpty()) {
            errors.put("buyerPhone", "Buyer phone cannot be empty.");
        }
        if (buyerName == null || buyerName.isEmpty()) {
            errors.put("buyerName", "Buyer name cannot be empty.");
        }
        if (addressUrl == null || addressUrl.isEmpty()) {
            errors.put("addressUrl", "Address Url cannot be empty.");
        }
        if (paymentMethod == null) {
            errors.put("paymentMethod", "Payment method cannot be empty.");
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException("Bad request", Error.build("Invalid order request", errors));
        }
        addressUrl = addressUrl.trim();
        note = note != null ? note.trim() : null;
    }
}
