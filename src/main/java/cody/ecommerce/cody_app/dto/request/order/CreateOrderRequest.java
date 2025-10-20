package cody.ecommerce.cody_app.dto.request.order;

import cody.ecommerce.cody_app.constant.PaymentMethodEnum;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.request.product.CreateProductImageDTO;
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
    private String customComboName;
    private String note;
    private Boolean isCombo;
    private PaymentMethodEnum paymentMethod;
    private String sellerId;

    public void validate() {
        Map<String, String> errors = new HashMap<>();
        if (items == null || items.isEmpty()) {
            errors.put("items", "Đơn hàng không được trống.");
        }
        if (buyerPhone == null || buyerPhone.isEmpty()) {
            errors.put("buyerPhone", "Số điện thoại không được để trống.");
        }
        if (buyerName == null || buyerName.isEmpty()) {
            errors.put("buyerName", "Tên người nhận không được để trống.");
        }
        if (addressUrl == null || addressUrl.isEmpty()) {
            errors.put("addressUrl", "Địa chỉ nhận hàng không được để trống.");
        }
        if (paymentMethod == null) {
            errors.put("paymentMethod", "Phương thức thanh toán không được để trống.");
        }
        if (isCombo == null) {
            isCombo = false;
            if (customComboName != null && !customComboName.isEmpty()) {
                errors.put("customComboName", "Không được đặt tên combo nếu không phải là combo.");
            }
        } else if (customComboName == null || customComboName.isEmpty()) {
            errors.put("customComboName", "Combo phải có tên.");
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException("Bad request", Error.build("Invalid order request", errors));
        }
        addressUrl = addressUrl.trim();
        note = note != null ? note.trim() : null;
    }
}
