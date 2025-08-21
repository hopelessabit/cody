package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.constant.OrderMainStatusEnum;
import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.constant.OrderStatusDeliveryEnum;
import cody.ecommerce.cody_app.entity.Order;
import cody.ecommerce.cody_app.entity.sub_entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String orderId;
    private List<OrderItemDTO> items;
    private UserDTO seller;
    private UserDTO buyer;
    private BigDecimal totalPrice;
    private StatusDTO<OrderMainStatusEnum> status;
    private StatusDTO<OrderStatusDeliveryEnum> deliveryStatus;
    private StatusDTO<OrderPaymentStatusEnum> paymentStatus;

    public static OrderDTO from(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getId());
        orderDTO.setItems(order.getOrderItems().stream().map(OrderItemDTO::from).toList());
        orderDTO.setSeller(UserDTO.fromBasic(order.getSeller()));
        orderDTO.setBuyer(UserDTO.fromBasic(order.getBuyer()));
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setStatus(StatusDTO.from(order.getMainStatus()));
        List<OrderStatus> orderStatuses = order.getOrderStatuses().stream().sorted(Comparator.comparing(OrderStatus::getModifiedAt)).toList();
        orderDTO.setDeliveryStatus(StatusDTO.from(orderStatuses.get(0).getDeliveryStatus()));
        orderDTO.setPaymentStatus(StatusDTO.from(orderStatuses.get(0).getPaymentStatus()));
        return orderDTO;
    }
}
