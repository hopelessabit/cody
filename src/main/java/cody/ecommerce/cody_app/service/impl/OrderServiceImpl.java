package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.OrderMainStatusEnum;
import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.constant.OrderStatusDeliveryEnum;
import cody.ecommerce.cody_app.constant.PaymentMethodEnum;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.OrderDTO;
import cody.ecommerce.cody_app.dto.request.order.CreateOrderItemRequest;
import cody.ecommerce.cody_app.dto.request.order.CreateOrderRequest;
import cody.ecommerce.cody_app.dto.request.order.UpdateOrderAddressRequest;
import cody.ecommerce.cody_app.dto.request.order.UpdateOrderStatusRequest;
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
                    Product product = products.stream().filter(product1 -> product1.getId().equals(item.getProductId())).findAny().orElseThrow(() -> new BadRequestException("Product not found"));
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
        productRepository.saveAll(products);
        return OrderDTO.from(resultOrder);
    }

    @Transactional
    public OrderDTO updateAddress(UpdateOrderAddressRequest request, String orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BadRequestException("Order not found", Error.build(orderId)));
        if (!order.getBuyerId().equals(SecurityContextHolderUtil.getAccount().getId())) {
            throw new BadRequestException("Unauthorized", Error.build("You are not authorized to update this order", List.of(orderId)));
        }

        OrderStatus latestStatus = orderStatusRepository.findFirstByOrderIdOrderByModifiedAtDesc(orderId).orElseThrow(
                () -> new BadRequestException("Order status not found", Error.build("Order status not found", List.of(orderId)))
        );

        if (!latestStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.PND)){
            throw new BadRequestException("Bad request", Error.build("Order cannot be updated", List.of(orderId)));
        }

        order.setAddressUrl(request.getAddressUrl());
        return OrderDTO.from(orderRepository.save(order));
    }

    public OrderDTO getById(String id) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Order not found", Error.build("Order not found", List.of(id))));
        return OrderDTO.from(order);
    }

    @Transactional
    public Void userCancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new BadRequestException("Order not found", Error.build("Order not found", List.of(orderId))));
        OrderStatus latestStatus = orderStatusRepository.findFirstByOrderIdOrderByModifiedAtDesc(orderId).orElseThrow(
                () -> new BadRequestException("Order status not found", Error.build("Order status not found", List.of(orderId)))
        );

        if (!order.getBuyerId().equals(SecurityContextHolderUtil.getAccount().getId())) {
            throw new BadRequestException("Unauthorized", Error.build("You are not authorized to cancel this order", List.of(orderId)));
        }

        if (order.getMainStatus().isPending() && !latestStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.PND)) {
            throw new BadRequestException("Bad request", Error.build("Order cannot be cancelled", List.of(orderId)));
        }

        order.setMainStatus(OrderMainStatusEnum.CN);
        orderRepository.save(order);

        OrderStatus newStatus = new OrderStatus(latestStatus, SecurityContextHolderUtil.getAccount());
        newStatus.setDeliveryStatus(OrderStatusDeliveryEnum.CNL);

        orderStatusRepository.save(newStatus);
        return null;
    }

    @Transactional
    public Void staffUpdateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new BadRequestException("Order not found", Error.build("Order not found", List.of(orderId))));
        OrderStatus latestOrderStatus = orderStatusRepository.findFirstByOrderIdOrderByModifiedAtDesc(orderId).orElseThrow(
                () -> new BadRequestException("Order status not found", Error.build("Order status not found", List.of(orderId)))
        );
        OrderStatus resultStatus = null;
        if (request.getDeliveryStatus() != null && request.getPaymentStatus() != null) {
            if (request.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DLD) && request.getPaymentStatus().equals(OrderPaymentStatusEnum.PD)) {
                resultStatus = updateOrderDeliveringStatus(order, latestOrderStatus, request.getDeliveryStatus());
                resultStatus = updateOrderPaymentStatus(order, resultStatus, PaymentMethodEnum.COD);
            } else if (request.getDeliveryStatus().isCancelOrDeclinedOrDeliveryReturnedOrReturnConfirmed() && request.getPaymentStatus().equals(OrderPaymentStatusEnum.RFG)) {
                resultStatus = updateOrderDeliveringStatus(order, latestOrderStatus, request.getDeliveryStatus());
                resultStatus = updateOrderPaymentStatus(order, resultStatus, null);
            }
        } else if (request.getDeliveryStatus() != null) {
            resultStatus = updateOrderDeliveringStatus(order, latestOrderStatus, request.getDeliveryStatus());
        } else if (request.getPaymentStatus() != null) {
            resultStatus = modifyOrderPaymentStatus(order, latestOrderStatus, request.getPaymentStatus());
        } else {
            throw new BadRequestException("Bad request", Error.build("Invalid request", List.of("Delivery status or payment status must be provided")));
        }
        if (resultStatus == null)
            return null;
        modifyOrderMainStatus(order, resultStatus);
        orderStatusRepository.save(resultStatus);
        orderRepository.save(order);
        return null;
    }

    @Transactional
    public Void userReturnOrder(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new BadRequestException("Order not found", Error.build("Order not found", List.of(orderId))));
        OrderStatus latestOrderStatus = orderStatusRepository.findFirstByOrderIdOrderByModifiedAtDesc(orderId).orElseThrow(
                () -> new BadRequestException("Order status not found", Error.build("Order status not found", List.of(orderId)))
        );
        if (!order.getBuyerId().equals(SecurityContextHolderUtil.getAccount().getId())) {
            throw new BadRequestException("Unauthorized", Error.build("You are not authorized to return this order", List.of(orderId)));
        }
        if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DLD)) {
            throw new BadRequestException("Bad request", Error.build("Order cannot be returned", List.of(orderId)));
        }
        return null;
    }

    @Override
    public Void userConfirmOrderCompleted(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new BadRequestException("Order not found", Error.build("Order not found", List.of(orderId))));
        OrderStatus latestOrderStatus = orderStatusRepository.findFirstByOrderIdOrderByModifiedAtDesc(orderId).orElseThrow(
                () -> new BadRequestException("Order status not found", Error.build("Order status not found", List.of(orderId)))
        );
        if (!order.getBuyerId().equals(SecurityContextHolderUtil.getAccount().getId())) {
            throw new BadRequestException("Unauthorized", Error.build("You are not authorized to confirm this order", List.of(orderId)));
        }
        Map<String, String> errors = new HashMap<>();
        if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DLN)) {
            errors.put("delivery_status", "Order must be delivered before confirmation");
        }
        if (!latestOrderStatus.getPaymentStatus().equals(OrderPaymentStatusEnum.PD)) {
            errors.put("payment_status", "Order must be paid before confirmation");
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException("Bad request", Error.build("Order cannot be confirmed", errors)));
        }

        OrderStatus result = new OrderStatus(latestOrderStatus, SecurityContextHolderUtil.getAccount());
        result.setDeliveryStatus(OrderStatusDeliveryEnum.U_CF);
        orderStatusRepository.save(result);

        order.setMainStatus(OrderMainStatusEnum.CP);
        orderRepository.save(order);
        return null;
    }

    public void modifyOrderMainStatus(Order order, OrderStatus latestOrderStatus) {
        if (latestOrderStatus == null)
            return;
        if (latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DC)) {
            order.setMainStatus(OrderMainStatusEnum.DC);
        } else if (latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.CNL)) {
            order.setMainStatus(OrderMainStatusEnum.CN);
        } else if (latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.CF)) {
            order.setMainStatus(OrderMainStatusEnum.CF);
        } else if (latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DLN)) {
            order.setMainStatus(OrderMainStatusEnum.DL);
        } else if (latestOrderStatus.getPaymentStatus().equals(OrderPaymentStatusEnum.RFG)) {
            order.setMainStatus(OrderMainStatusEnum.RFG);
        } else if (latestOrderStatus.getPaymentStatus().equals(OrderPaymentStatusEnum.RFD)) {
            order.setMainStatus(OrderMainStatusEnum.RFD);
        }
    }

    public OrderStatus updateOrderPaymentStatus(Order order, OrderStatus latestOrderStatus, PaymentMethodEnum paymentMethod) {
        OrderStatus result = null;
        switch (latestOrderStatus.getDeliveryStatus()){
            case PND -> {
                if (latestOrderStatus.getPaymentStatus().equals(OrderPaymentStatusEnum.UP)) {
                    if (paymentMethod.isCOD()) {
                        result = modifyOrderPaymentStatus(order, latestOrderStatus, OrderPaymentStatusEnum.C_UP);
                    } else {
                        result = modifyOrderPaymentStatus(order, latestOrderStatus, OrderPaymentStatusEnum.PD);
                    }
                } else
                    throw new BadRequestException("Bad request", Error.build("Order is paid", List.of(order.getId())));
            }
            case DLD -> {
                if (latestOrderStatus.getPaymentStatus().equals(OrderPaymentStatusEnum.C_UP)){
                    result = modifyOrderPaymentStatus(order, latestOrderStatus, OrderPaymentStatusEnum.PD);
                }
            }
            case DC,D_RT, R_CF -> {
                if (latestOrderStatus.getPaymentStatus().equals(OrderPaymentStatusEnum.PD)) {
                    result = modifyOrderPaymentStatus(order, latestOrderStatus, OrderPaymentStatusEnum.RFG);
                }
                else if (latestOrderStatus.getPaymentStatus().equals(OrderPaymentStatusEnum.RFG)) {
                    result = modifyOrderPaymentStatus(order, latestOrderStatus, OrderPaymentStatusEnum.RFD);
                } else {
                    throw new BadRequestException("Bad request", Error.build("Order cannot be updated", List.of(order.getId())));
                }
            }
            default -> {
                throw new BadRequestException("Bad request", Error.build("Order cannot be updated", List.of(order.getId())));
            }
        }
        return result;
    }

    public OrderStatus modifyOrderPaymentStatus(Order order, OrderStatus latestOrderStatus, OrderPaymentStatusEnum paymentStatus) {
        OrderStatus newStatus = new OrderStatus(latestOrderStatus, SecurityContextHolderUtil.getAccount());
        newStatus.setPaymentStatus(paymentStatus);
        return newStatus;
    }

    public OrderStatus updateOrderDeliveringStatus(Order order, OrderStatus latestOrderStatus, OrderStatusDeliveryEnum deliveryStatus) {
        OrderStatus result = null;
        boolean isValid = true;
        switch (deliveryStatus) {
            case CF -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.PND))
                    isValid = false;
            }
            case DLN -> {
                if (!(latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.PND)
                        || latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.R_PD)))
                    isValid = false;
            }
            case DLD -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DLN))
                    isValid = false;
            }
            case U_CF, D_FL -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DLD))
                    isValid = false;
            }
            case D_RG -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.D_FL))
                    isValid = false;
            }
            case D_RT -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.D_RG))
                    isValid = false;
            }
            case R_PD -> {
                if (!(latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.U_CF)
                        || latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DLD)))
                    isValid = false;
            }
            case R_CF -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.R_PD))
                    isValid = false;
            }
            case R_DLN -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.R_CF))
                    isValid = false;
            }
            case R_DLD -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.R_DLN))
                    isValid = false;
            }
            default -> throw new BadRequestException("Bad request", Error.build("Invalid delivery status", List.of(deliveryStatus.name())));
        }
        if (!isValid) {
            throw new BadRequestException("Bad request",
                    Error.build("Order cannot be updated",
                            Map.of("before_delivery_status", latestOrderStatus.getDeliveryStatus().getFullName(),
                                    "after_delivery_status", deliveryStatus.getFullName())));
        }
        result = modifyOrderDeliveringStatus(order, latestOrderStatus, deliveryStatus);
        return result;
    }

    public OrderStatus modifyOrderDeliveringStatus(Order order, OrderStatus latestOrderStatus, OrderStatusDeliveryEnum deliveryStatus) {
        OrderStatus newStatus = new OrderStatus(latestOrderStatus, SecurityContextHolderUtil.getAccount());
        newStatus.setDeliveryStatus(deliveryStatus);
        return newStatus;
    }
}
