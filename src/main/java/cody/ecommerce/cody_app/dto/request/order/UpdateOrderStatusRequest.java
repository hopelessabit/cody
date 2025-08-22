package cody.ecommerce.cody_app.dto.request.order;

import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.constant.OrderStatusDeliveryEnum;
import cody.ecommerce.cody_app.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequest {
    private OrderStatusDeliveryEnum deliveryStatus;
    private OrderPaymentStatusEnum paymentStatus;

    public void validate() {
        if (deliveryStatus == null && paymentStatus == null) {
            throw new BadRequestException("At least one of deliveryStatus or paymentStatus must be provided.");
        }
    }
}
