package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.constant.OrderMainStatusEnum;
import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.constant.OrderStatusDeliveryEnum;
import cody.ecommerce.cody_app.dto.OrderDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.order.CreateOrderRequest;
import cody.ecommerce.cody_app.dto.request.order.UpdateOrderAddressRequest;
import cody.ecommerce.cody_app.dto.request.order.UpdateOrderStatusRequest;
import cody.ecommerce.cody_app.service.OrderService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders/create")
    public ResponseEntity<ResponseData<OrderDTO>> createOrder(@RequestBody CreateOrderRequest request) {
        return ResponseUtil.getResponse(() -> orderService.create(request), "Order created successfully");
    }

    @GetMapping("/orders/")
    public ResponseEntity<ResponseData<Page<OrderDTO>>> getAllUserOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {
        return ResponseUtil.getResponse(() -> orderService.getAllUserOrders(page, size, sortBy, sortDirection), "Orders retrieved successfully");
    }

    @PutMapping("/orders/{orderId}/paid")
    public ResponseEntity<ResponseData<Void>> markOrderAsPaid(@PathVariable String orderId) {
        return ResponseUtil.getResponse(() -> orderService.markOrderAsPaid(orderId), "Order marked as paid successfully");
    }

    @GetMapping("/admin/orders/get-all")
    public ResponseEntity<ResponseData<Page<OrderDTO>>> getAllOrders(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) String buyerId,
            @RequestParam(required = false) String buyerName,
            @RequestParam(required = false) String buyerPhone,
            @RequestParam(required = false) String sellerId,
            @RequestParam(required = false) String sellerName,
            @RequestParam(required = false) OrderMainStatusEnum mainStatus,
            @RequestParam(required = false) OrderStatusDeliveryEnum deliveryStatus,
            @RequestParam(required = false) OrderPaymentStatusEnum paymentStatus,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String startDateTime,
            @RequestParam(required = false) String endDateTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        return ResponseUtil.getResponse(() -> orderService.getAllOrders(
                orderId, buyerId, buyerName, buyerPhone, sellerId, sellerName,
                mainStatus, deliveryStatus, paymentStatus, minPrice, maxPrice,
                startDateTime, endDateTime, page, size, sortBy, sortDirection
        ), "Orders retrieved successfully");
    }
    
    @PutMapping("/orders/{orderId}/address")
    public ResponseEntity<ResponseData<Void>> updateOrderAddress(
            @PathVariable String orderId,
            @RequestBody UpdateOrderAddressRequest request) {
        return ResponseUtil.getResponse(() -> orderService.updateAddress(request, orderId), "Order address updated successfully");
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ResponseData<OrderDTO>> getOrderById(@PathVariable String orderId) {
        return ResponseUtil.getResponse(() -> orderService.getById(orderId), "Order retrieved successfully");
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ResponseData<Void>> cancelOrder(@PathVariable String orderId) {
        return ResponseUtil.getResponse(() -> orderService.userCancelOrder(orderId), "Order cancelled successfully");
    }

    @PostMapping("/orders/{orderId}/confirm")
    public ResponseEntity<ResponseData<Void>> confirmOrder(@PathVariable String orderId) {
        return ResponseUtil.getResponse(() -> orderService.userConfirmOrderCompleted(orderId), "Order confirmed successfully");
    }

    @PostMapping("/orders/{orderId}/return")
    public ResponseEntity<ResponseData<Void>> returnOrder(@PathVariable String orderId) {
        return ResponseUtil.getResponse(() -> orderService.userReturnOrder(orderId), "Order return request sent successfully");
    }

    @PostMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<ResponseData<Void>> adminUpdateOrderStatus(
            @PathVariable String orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        return ResponseUtil.getResponse(() -> orderService.staffUpdateOrderStatus(orderId, request), "Order status updated successfully");
    }
}
