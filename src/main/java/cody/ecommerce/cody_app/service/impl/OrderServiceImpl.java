package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.OrderDTO;
import cody.ecommerce.cody_app.dto.request.order.CreateOrderItemRequest;
import cody.ecommerce.cody_app.dto.request.order.CreateOrderRequest;
import cody.ecommerce.cody_app.entity.Order;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.entity.sub_entity.OrderItem;
import cody.ecommerce.cody_app.entity.sub_entity.OrderStatus;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.repository.*;
import cody.ecommerce.cody_app.service.OrderService;
import cody.ecommerce.cody_app.util.SecurityContextHolderUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderDTO create(CreateOrderRequest request) {
        User buyer = SecurityContextHolderUtil.getAccount();
        // validate request
        request.validate();
        // 1. Check each product exists
        List<String> requestProductIds = request.getItems().stream()
                .map(CreateOrderItemRequest::getProductId)
                .toList();
        List<Product> products = productRepository.findAllById(requestProductIds);
        if (products.size() != request.getItems().size()) {
            List<String> missingProductIds = new ArrayList<>();
            requestProductIds.forEach(itemId -> {
                if (!requestProductIds.contains(itemId)) {
                    missingProductIds.add(itemId);
                }
            });
            if (!missingProductIds.isEmpty()) {
                throw new BadRequestException("Bad request", Error.build("Products not found", missingProductIds));
            }
        }
        Map<String, String> errors = new HashMap<>();
        // Process order creation
        Order order = Order.from(request);
        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId()).get();
                    if (product.getStockQuantity() < item.getQuantity()) {
                        errors.put("productId", "Not enough stock for product: " + product.getId());
                    }
                    product.setStockQuantity(product.getStockQuantity() - item.getQuantity()); // Update stock
                    return OrderItem.from(order.getId(), product, item);
                }).toList();
        if (!errors.isEmpty()) {
            throw new BadRequestException("Bad request", Error.build("Not enough items", errors));
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            totalPrice = totalPrice.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalPrice(totalPrice);

        order.setBuyerId(buyer.getId());
        if (request.getSellerId() != null) {
            User seller = userRepository.findById(request.getSellerId()).orElseThrow(() -> new BadRequestException("SellerId not found", Error.build("SellerId not found", List.of(request.getSellerId()))));
            order.setSellerId(seller.getId());
        }

        OrderStatus orderStatus ;
        if (request.getPaymentMethod().isCOD()){
            orderStatus = new OrderStatus(order, OrderPaymentStatusEnum.C_UP, buyer);
        } else
            orderStatus = new OrderStatus(order, OrderPaymentStatusEnum.UP, buyer);

        Order resultOrder = orderRepository.save(order);
        Set<OrderStatus> resultOrderStatuses =  new HashSet<>(List.of(orderStatusRepository.save(orderStatus)));
        resultOrder.setOrderStatuses(resultOrderStatuses);
        Set<OrderItem> resultOrderItems = new HashSet<>(orderItemRepository.saveAll(orderItems));
        resultOrder.setOrderItems(resultOrderItems);
        return OrderDTO.from(resultOrder);
    }
}
