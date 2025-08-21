package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.OrderDTO;
import cody.ecommerce.cody_app.dto.request.order.CreateOrderRequest;

public interface OrderService {
    OrderDTO create(CreateOrderRequest request);
}
