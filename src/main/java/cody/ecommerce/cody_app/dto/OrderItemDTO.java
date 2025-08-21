package cody.ecommerce.cody_app.dto;

import cody.ecommerce.cody_app.entity.sub_entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private ProductDTO product;
    private Integer quantity;
    private BigDecimal price;

    public OrderItemDTO(OrderItem orderItem) {
        this.product = ProductDTO.basicList(orderItem.getProduct());
        this.quantity = orderItem.getQuantity();
        this.price = orderItem.getPrice();
    }

    public static OrderItemDTO from(OrderItem orderItem) {
        return new OrderItemDTO(orderItem);
    }
}
