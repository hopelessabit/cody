package cody.ecommerce.cody_app.entity.sub_entity;

import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.constant.OrderStatusDeliveryEnum;
import cody.ecommerce.cody_app.entity.Order;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.entity.sub_entity_id.OrderStatusId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(OrderStatusId.class)
public class OrderStatus {

    @Id
    @Column(name = "order_id", nullable = false, length = 50)
    private String orderId;

    @Id
    @Column(name = "delivery_status", length = 50)
    @Enumerated(EnumType.STRING)
    private OrderStatusDeliveryEnum deliveryStatus;

    @Id
    @Column(name = "payment_status", length = 50)
    @Enumerated(EnumType.STRING)
    private OrderPaymentStatusEnum paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifier_id", referencedColumnName = "id", nullable = false, insertable = false, updatable = false)
    private User modifier;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    public OrderStatus(Order order, OrderPaymentStatusEnum paymentStatus, User modifier) {
        this.orderId = order.getId();
        this.deliveryStatus = OrderStatusDeliveryEnum.PND; // Default delivery status
        this.paymentStatus = paymentStatus;
        this.modifier = modifier;
    }
}