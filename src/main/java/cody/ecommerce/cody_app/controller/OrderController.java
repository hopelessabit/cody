package cody.ecommerce.cody_app.controller;

import cody.ecommerce.cody_app.dto.OrderDTO;
import cody.ecommerce.cody_app.dto.ResponseData;
import cody.ecommerce.cody_app.dto.request.order.CreateOrderRequest;
import cody.ecommerce.cody_app.dto.request.order.UpdateOrderAddressRequest;
import cody.ecommerce.cody_app.dto.request.order.UpdateOrderStatusRequest;
import cody.ecommerce.cody_app.service.OrderService;
import cody.ecommerce.cody_app.util.ResponseUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<ResponseData<OrderDTO>> createOrder(@RequestBody CreateOrderRequest request) {
        return ResponseUtil.getResponse(() -> orderService.create(request), "Order created successfully");
    }
    
    @PutMapping("/{orderId}/address")
    public ResponseEntity<ResponseData<OrderDTO>> updateOrderAddress(
            @PathVariable String orderId,
            @RequestBody UpdateOrderAddressRequest request) {
        return ResponseUtil.getResponse(() -> orderService.updateAddress(request, orderId), "Order address updated successfully");
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ResponseData<OrderDTO>> getOrderById(@PathVariable String orderId) {
        return ResponseUtil.getResponse(() -> orderService.getById(orderId), "Order retrieved successfully");
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<ResponseData<Void>> cancelOrder(@PathVariable String orderId) {
        return ResponseUtil.getResponse(() -> orderService.userCancelOrder(orderId), "Order cancelled successfully");
    }

    @PutMapping("/confirm/{orderId}")
    public ResponseEntity<ResponseData<Void>> confirmOrder(@PathVariable String orderId) {
        return ResponseUtil.getResponse(() -> orderService.userConfirmOrderCompleted(orderId), "Order confirmed successfully");
    }

    @PutMapping("/return/{orderId}")
    public ResponseEntity<ResponseData<Void>> returnOrder(@PathVariable String orderId) {
        return ResponseUtil.getResponse(() -> orderService.userReturnOrder(orderId), "Order return request sent successfully");
    }

    @PutMapping("/admin/status/{orderId}")
    public ResponseEntity<ResponseData<Void>> adminUpdateOrderStatus(
            @PathVariable String orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        return ResponseUtil.getResponse(() -> orderService.staffUpdateOrderStatus(orderId, request), "Order status updated successfully");
    }
}
