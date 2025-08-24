package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.constant.OrderMainStatusEnum;
import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.constant.OrderStatusDeliveryEnum;
import cody.ecommerce.cody_app.dto.request.order.OrderStatusDTO;
import cody.ecommerce.cody_app.entity.Order;
import cody.ecommerce.cody_app.entity.sub_entity.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private String orderId;
    private List<OrderItemDTO> items;
    private String addressUrl;
    private List<OrderStatusDTO> orderStatuses;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String declineReason;
    private UserDTO seller;
    private UserDTO buyer;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;
    private StatusDTO<OrderMainStatusEnum> status;
    private StatusDTO<OrderStatusDeliveryEnum> deliveryStatus;
    private StatusDTO<OrderPaymentStatusEnum> paymentStatus;

    public static OrderDTO from(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getId());
        orderDTO.setItems(order.getOrderItems().stream().map(OrderItemDTO::from).toList());
        orderDTO.setSeller(UserDTO.fromBasic(order.getSeller()));
        orderDTO.setBuyer(UserDTO.fromBasic(order.getBuyer(), order.getBuyerPhone()));
        orderDTO.setTotalPrice(order.getTotalPrice());
        orderDTO.setAddressUrl(order.getAddressUrl());
        orderDTO.setDeclineReason(order.getDeclineReason());
        orderDTO.setCreatedAt(order.getCreatedAt());
        orderDTO.setStatus(StatusDTO.from(order.getMainStatus()));
        List<OrderStatus> orderStatuses = order.getOrderStatuses().stream().sorted(Comparator.comparing(OrderStatus::getModifiedAt)).toList();
        orderDTO.setOrderStatuses(orderStatuses.stream().map(OrderStatusDTO::from).toList());
        if (!order.getOrderStatuses().isEmpty()) {
            List<OrderStatus> sortedStatuses = order.getOrderStatuses().stream()
                    .sorted(Comparator.comparing(OrderStatus::getModifiedAt).reversed())
                    .toList();

            OrderStatus latestStatus = sortedStatuses.get(0); // First item is now the latest

            orderDTO.setDeliveryStatus(StatusDTO.from(latestStatus.getDeliveryStatus()));
            orderDTO.setPaymentStatus(StatusDTO.from(latestStatus.getPaymentStatus()));
        }
        return orderDTO;
    }
}
