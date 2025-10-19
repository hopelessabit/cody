package cody.ecommerce.cody_app.service.impl;

import cody.ecommerce.cody_app.constant.OrderMainStatusEnum;
import cody.ecommerce.cody_app.constant.OrderPaymentStatusEnum;
import cody.ecommerce.cody_app.constant.OrderStatusDeliveryEnum;
import cody.ecommerce.cody_app.entity.Category;
import cody.ecommerce.cody_app.constant.PaymentMethodEnum;
import cody.ecommerce.cody_app.dto.Error;
import cody.ecommerce.cody_app.dto.OrderDTO;
import cody.ecommerce.cody_app.dto.request.order.*;
import cody.ecommerce.cody_app.entity.Order;
import cody.ecommerce.cody_app.entity.Product;
import cody.ecommerce.cody_app.entity.User;
import cody.ecommerce.cody_app.entity.sub_entity.OrderItem;
import cody.ecommerce.cody_app.entity.sub_entity.OrderStatus;
import cody.ecommerce.cody_app.exception.BadRequestException;
import cody.ecommerce.cody_app.repository.*;
import cody.ecommerce.cody_app.service.EmployeeKpiService;
import cody.ecommerce.cody_app.service.OrderService;
import cody.ecommerce.cody_app.service.KpiService;
import cody.ecommerce.cody_app.util.SecurityContextHolderUtil;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserRepository userRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final KpiService kpiService;
    private final EmployeeKpiService employeeKpiService;

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

        // Fix: Properly check for missing products
        if (products.size() != request.getItems().size()) {
            List<String> foundProductIds = products.stream().map(Product::getId).toList();
            List<String> missingProductIds = requestProductIds.stream()
                    .filter(id -> !foundProductIds.contains(id))
                    .toList();

            if (!missingProductIds.isEmpty()) {
                throw new BadRequestException("Bad request", Error.build("Products not found", missingProductIds));
            }
        }

        // 2. Check all products have the same category (if multiple products)
        if (products.size() > 1) {
            // Get first product's categories
            Set<String> firstProductCategoryIds = products.get(0).getCategories().stream()
                    .map(Category::getId)
                    .collect(java.util.stream.Collectors.toSet());

            // Check if all products share at least one common category
            boolean allHaveCommonCategory = products.stream().allMatch(product -> {
                Set<String> productCategoryIds = product.getCategories().stream()
                        .map(Category::getId)
                        .collect(java.util.stream.Collectors.toSet());
                // Check if there's any intersection between categories
                return productCategoryIds.stream().anyMatch(firstProductCategoryIds::contains);
            });

            if (!allHaveCommonCategory) {
                Map<String, String> categoryErrors = new HashMap<>();
                categoryErrors.put("category", "All products in an order must share at least one common category");
                throw new BadRequestException("Bad request", Error.build("Category mismatch", categoryErrors));
            }
        }

        Map<String, String> errors = new HashMap<>();
        // Process order creation
        Order order = Order.from(request);

        // Set isCombo to true if:
        // 1. User chose multiple products, OR
        // 2. User chose a single product that is itself a combo (isCombo = true)
        boolean orderIsCombo = false;

        if (products.size() > 1) {
            // Multiple products selected
            orderIsCombo = true;
        } else if (products.size() == 1) {
            // Single product selected - check if it's a combo product
            Product singleProduct = products.get(0);
            if (singleProduct.getIsCombo() != null && singleProduct.getIsCombo()) {
                orderIsCombo = true;
            }
        }

        order.setIsCombo(orderIsCombo);

        List<OrderItem> orderItems = request.getItems().stream()
                .map(item -> {
                    Product product = products.stream()
                            .filter(p -> p.getId().equals(item.getProductId()))
                            .findFirst()
                            .orElseThrow(() -> new BadRequestException("Product not found"));

                    if (product.getStockQuantity() < item.getQuantity()) {
                        errors.put("productId_" + product.getId(), "Not enough stock for product: " + product.getId());
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
    public Void updateAddress(UpdateOrderAddressRequest request, String orderId){
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
        return null;
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
        if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DLD)) {
            errors.put("delivery_status", "Order must be delivered before confirmation");
        }
        if (!latestOrderStatus.getPaymentStatus().equals(OrderPaymentStatusEnum.PD)) {
            errors.put("payment_status", "Order must be paid before confirmation");
        }
        if (!errors.isEmpty()) {
            throw new BadRequestException("Bad request", Error.build("Order cannot be confirmed", errors));
        }

        OrderStatus result = new OrderStatus(latestOrderStatus, SecurityContextHolderUtil.getAccount());
        result.setDeliveryStatus(OrderStatusDeliveryEnum.U_CF);
        orderStatusRepository.save(result);

        OrderMainStatusEnum previousStatus = order.getMainStatus();
        order.setMainStatus(OrderMainStatusEnum.CP);
        orderRepository.save(order);

        // Update KPI progress when order is completed
        if (previousStatus != OrderMainStatusEnum.CP && order.getSellerId() != null) {
            updateEmployeeKpiProgress(order);
        }

        return null;
    }

    @Override
    public Page<OrderDTO> getAllUserOrders(int page, int size, String sortBy, String sortDirection) {
        User user = SecurityContextHolderUtil.getAccount();

        // Validate and set defaults
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "createAt";
        if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "DESC";

        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Order> orderPage = null;
        orderPage = orderRepository.findByBuyerId(user.getId(), pageable);
        return orderPage.map(OrderDTO::from);
    }

    public void modifyOrderMainStatus(Order order, OrderStatus latestOrderStatus) {
        if (latestOrderStatus == null)
            return;

        OrderMainStatusEnum previousStatus = order.getMainStatus();

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

        // Check if order status changed to CP (Completed) and update KPI
        if (order.getMainStatus() == OrderMainStatusEnum.CP &&
            previousStatus != OrderMainStatusEnum.CP &&
            order.getSellerId() != null) {
            updateEmployeeKpiProgress(order);
        }
    }

    /**
     * Updates employee KPI progress when an order is completed.
     * Calculates total product quantity and updates selled progress for the employee.
     */
    private void updateEmployeeKpiProgress(Order order) {
        try {
            // Calculate total product quantity from order items
            int totalProductQuantity = order.getOrderItems().stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            // Update KPI progress for the seller
            kpiService.updateSelledProgressForEmployee(order.getSellerId(), totalProductQuantity);

            // Also update EmployeeKpi stats directly
            if (employeeKpiService instanceof EmployeeKpiServiceImpl) {
                ((EmployeeKpiServiceImpl) employeeKpiService).updateStatsAfterKpiChange(order.getSellerId());
            }

            log.info("Updated KPI progress for employee {} with {} additional sales from order {}",
                    order.getSellerId(), totalProductQuantity, order.getId());
        } catch (Exception e) {
            log.error("Failed to update KPI progress for employee {} from order {}: {}",
                    order.getSellerId(), order.getId(), e.getMessage(), e);
            // Don't throw exception to avoid breaking order completion flow
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
                } else throw new BadRequestException("Order must be paid via banking");
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
        };

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
            case DC -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.PND)) {
                    isValid = false;
                    order.setMainStatus(OrderMainStatusEnum.DC);
                }
            }
            case DLN -> {
                if (!latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.CF))
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
                if (latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.U_CF)
                        || latestOrderStatus.getDeliveryStatus().equals(OrderStatusDeliveryEnum.DLD))
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

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(String orderId, String buyerId, String buyerName, String buyerPhone,
                                       String sellerId, String sellerName, OrderMainStatusEnum mainStatus,
                                       OrderStatusDeliveryEnum deliveryStatus, OrderPaymentStatusEnum paymentStatus,
                                       BigDecimal minPrice, BigDecimal maxPrice, String startDateTime, String endDateTime,
                                       int page, int size, String sortBy, String sortDirection) {

        // Validation code remains the same...
        User currentUser = SecurityContextHolderUtil.getAccount();
        if (currentUser.getRole().isUser()) {
            throw new BadRequestException("Unauthorized access", Error.build("role", List.of("Admin or staff role required")));
        }

        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 10;
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "createdAt";
        if (sortDirection == null || sortDirection.trim().isEmpty()) sortDirection = "DESC";

        // Date parsing and validation...
        LocalDateTime parsedStartDateTime = null;
        LocalDateTime parsedEndDateTime = null;
        try {
            if (startDateTime != null && !startDateTime.trim().isEmpty()) {
                parsedStartDateTime = LocalDateTime.parse(startDateTime.trim());
            }
            if (endDateTime != null && !endDateTime.trim().isEmpty()) {
                parsedEndDateTime = LocalDateTime.parse(endDateTime.trim());
            }
            if (parsedStartDateTime != null && parsedEndDateTime != null && parsedStartDateTime.isAfter(parsedEndDateTime)) {
                throw new BadRequestException("Invalid date range", Error.build("dateTime", List.of("Start date time cannot be after end date time")));
            }
        } catch (Exception e) {
            throw new BadRequestException("Invalid date time format", Error.build("dateTime", List.of("Use ISO-8601 format: yyyy-MM-ddTHH:mm:ss")));
        }


        Sort.Direction direction = "DESC".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Two-step approach to handle fetch joins with pagination
        // Step 1: Get filtered order IDs with pagination
        Specification<Order> filterSpec = buildOrderSpecificationWithStatus(orderId, buyerId, buyerName, buyerPhone,
                sellerId, sellerName, mainStatus, deliveryStatus, paymentStatus,
                minPrice, maxPrice, parsedStartDateTime, parsedEndDateTime);

        Page<Order> orderPage = orderRepository.findAll(filterSpec, pageable);

        if (!orderPage.hasContent()) {
            return orderPage.map(OrderDTO::from);
        }

        // Step 2: Fetch complete order data with all relations for the filtered orders
        List<String> orderIds = orderPage.getContent().stream()
                .map(Order::getId)
                .toList();

        List<Order> ordersWithFullData = orderRepository.findOrdersWithAllDataByIds(orderIds);

        // Maintain pagination order
        Map<String, Order> orderMap = ordersWithFullData.stream()
                .collect(Collectors.toMap(Order::getId, order -> order));

        List<OrderDTO> orderedResults = orderIds.stream()
                .map(orderMap::get)
                .filter(Objects::nonNull)
                .map(OrderDTO::from)
                .toList();

        return new PageImpl<>(orderedResults, pageable, orderPage.getTotalElements());
    }

    @Override
    public Void markOrderAsPaid(String orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() ->
                new BadRequestException("Order not found", Error.build("Order not found", List.of(orderId))));
        log.info("isBank: {}, isAdmin: {}", SecurityContextHolderUtil.getRole().isAdmin(), SecurityContextHolderUtil.getRole().isAdmin());
        if (!SecurityContextHolderUtil.getRole().isBank() && !SecurityContextHolderUtil.getRole().isAdmin()) {
            throw new BadRequestException("Unauthorized", Error.build("You are not authorized to mark this order as paid"));
        }
        OrderStatus latestOrderStatus = orderStatusRepository.findFirstByOrderIdOrderByModifiedAtDesc(orderId).orElseThrow(
                () -> new BadRequestException("Order status not found", Error.build("Order status not found", List.of(orderId)))
        );

        OrderStatus newStatus = new OrderStatus(latestOrderStatus, SecurityContextHolderUtil.getAccount());
        if (!latestOrderStatus.getPaymentStatus().equals(OrderPaymentStatusEnum.UP)) {
            throw new BadRequestException("Bad request", Error.build("Order is already paid", List.of(orderId)));
        }
        newStatus.setPaymentStatus(OrderPaymentStatusEnum.PD);
        orderStatusRepository.save(newStatus);
        return null;
    }

    private Specification<Order> buildOrderSpecificationWithStatus(String orderId, String buyerId, String buyerName,
                                                                   String buyerPhone, String sellerId, String sellerName,
                                                                   OrderMainStatusEnum mainStatus, OrderStatusDeliveryEnum deliveryStatus,
                                                                   OrderPaymentStatusEnum paymentStatus, BigDecimal minPrice,
                                                                   BigDecimal maxPrice, LocalDateTime startDate, LocalDateTime endDate) {
        return (Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Basic filters
            if (orderId != null && !orderId.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("id")), "%" + orderId.toLowerCase() + "%"));
            }

            if (buyerId != null && !buyerId.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("buyerId"), buyerId));
            }

            if (buyerName != null && !buyerName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.join("buyer").get("name")), "%" + buyerName.toLowerCase() + "%"));
            }

            if (buyerPhone != null && !buyerPhone.trim().isEmpty()) {
                predicates.add(cb.like(root.get("buyerPhone"), "%" + buyerPhone + "%"));
            }

            if (sellerId != null && !sellerId.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("sellerId"), sellerId));
            }

            if (sellerName != null && !sellerName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.join("seller").get("name")), "%" + sellerName.toLowerCase() + "%"));
            }

            if (mainStatus != null) {
                predicates.add(cb.equal(root.get("mainStatus"), mainStatus));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalPrice"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("totalPrice"), maxPrice));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }

            // Status filtering using latest order status
            if (deliveryStatus != null || paymentStatus != null) {
                // Subquery to find the latest status timestamp for each order
                Subquery<LocalDateTime> latestStatusTimeSubquery = query.subquery(LocalDateTime.class);
                Root<OrderStatus> statusRoot = latestStatusTimeSubquery.from(OrderStatus.class);
                latestStatusTimeSubquery.select(cb.greatest(statusRoot.<LocalDateTime>get("modifiedAt")))
                        .where(cb.equal(statusRoot.get("orderId"), root.get("id")));

                // Subquery to get orders that have matching latest status
                Subquery<String> ordersWithMatchingStatusSubquery = query.subquery(String.class);
                Root<OrderStatus> matchingStatusRoot = ordersWithMatchingStatusSubquery.from(OrderStatus.class);

                List<Predicate> statusPredicates = new ArrayList<>();
                statusPredicates.add(cb.equal(matchingStatusRoot.get("modifiedAt"), latestStatusTimeSubquery));

                if (deliveryStatus != null) {
                    statusPredicates.add(cb.equal(matchingStatusRoot.get("deliveryStatus"), deliveryStatus));
                }

                if (paymentStatus != null) {
                    statusPredicates.add(cb.equal(matchingStatusRoot.get("paymentStatus"), paymentStatus));
                }

                ordersWithMatchingStatusSubquery.select(matchingStatusRoot.get("orderId"))
                        .where(cb.and(statusPredicates.toArray(new Predicate[0])));

                predicates.add(cb.in(root.get("id")).value(ordersWithMatchingStatusSubquery));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
