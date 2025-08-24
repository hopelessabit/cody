package cody.ecommerce.cody_app.dto.request.order;

import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.constant.OrderStatusDeliveryEnum;
import cody.ecommerce.cody_app.dto.StatusDTO;
import cody.ecommerce.cody_app.dto.UserDTO;
import cody.ecommerce.cody_app.entity.sub_entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDTO {
    private StatusDTO<OrderStatusDeliveryEnum> deliveryStatus;
    private StatusDTO<OrderPaymentStatusEnum> paymentStatus;
    private UserDTO modifiedBy;
    private String modifiedAt;

    public OrderStatusDTO(OrderStatus orderStatus) {
        this.deliveryStatus = StatusDTO.from(orderStatus.getDeliveryStatus());
        this.paymentStatus = StatusDTO.from(orderStatus.getPaymentStatus());
        this.modifiedAt = orderStatus.getModifiedAt().toString();
    }

    public static OrderStatusDTO from(OrderStatus orderStatus) {
        OrderStatusDTO result = new OrderStatusDTO(orderStatus);
        result.setModifiedBy(UserDTO.fromBasic(orderStatus.getModifier()));
        return result;
    }

    public static OrderStatusDTO basicList(OrderStatus orderStatus) {
        return new OrderStatusDTO(orderStatus);
    }
}
