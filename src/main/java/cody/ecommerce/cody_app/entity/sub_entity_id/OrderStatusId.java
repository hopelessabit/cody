package cody.ecommerce.cody_app.entity.sub_entity_id;

import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.constant.OrderStatusDeliveryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderStatusId implements Serializable {
    private String orderId;
    private OrderStatusDeliveryEnum deliveryStatus;
    private OrderPaymentStatusEnum paymentStatus;

}
