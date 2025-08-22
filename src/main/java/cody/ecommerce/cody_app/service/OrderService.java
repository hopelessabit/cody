package cody.ecommerce.cody_app.service;

import cody.ecommerce.cody_app.dto.OrderDTO;
import cody.ecommerce.cody_app.dto.request.order.CreateOrderRequest;
import cody.ecommerce.cody_app.dto.request.order.UpdateOrderAddressRequest;
import cody.ecommerce.cody_app.dto.request.order.UpdateOrderStatusRequest;
import cody.ecommerce.cody_app.entity.Order;
import cody.ecommerce.cody_app.exception.BadRequestException;

/**
 * Service interface for managing orders in the e-commerce application.
 *
 * This service provides comprehensive order management functionality including
 * order creation, status updates, address modifications, and order lifecycle management.
 * All operations respect user authorization and business rules.
 */
public interface OrderService {

    /**
     * Creates a new order based on the provided request.
     *
     * <p>This method performs the following operations:
     * <ul>
     *   <li>Validates the order request including items, buyer information, and payment method</li>
     *   <li>Verifies product availability and stock quantities</li>
     *   <li>Updates product stock levels by reducing quantities</li>
     *   <li>Calculates total order price based on item prices and quantities</li>
     *   <li>Creates order items and initial order status</li>
     *   <li>Sets appropriate payment status based on payment method (COD vs other methods)</li>
     * </ul>
     *
     * <p>The order is created with a main status of PENDING and payment status of:
     * <ul>
     *   <li>C_UP (COD-UNPAID) for Cash on Delivery orders</li>
     *   <li>UP (UNPAID) for other payment methods</li>
     * </ul>
     *
     * @param request the order creation request containing order details, items, and buyer information
     * @return OrderDTO representing the created order with all associated data
     * @throws BadRequestException if:
     *         <ul>
     *           <li>Request validation fails (missing required fields)</li>
     *           <li>Any requested products are not found</li>
     *           <li>Insufficient stock for any requested items</li>
     *           <li>Seller ID is provided but seller not found</li>
     *         </ul>
     * @throws SecurityException if user is not authenticated
     */
    OrderDTO create(CreateOrderRequest request);

    /**
     * Updates the delivery address for an existing order.
     *
     * <p>This operation is only allowed for orders that are still pending delivery
     * (delivery status = PND). Only the buyer who created the order can update the address.
     *
     * @param request the request containing the new address URL
     * @param orderId the ID of the order to update
     * @return {@link OrderDTO} entity with updated address information
     * @throws BadRequestException if:
     *         <ul>
     *           <li>Order with the given ID is not found</li>
     *           <li>Order status is not pending delivery</li>
     *           <li>Order status information is missing</li>
     *         </ul>
     * @throws SecurityException if:
     *         <ul>
     *           <li>User is not authenticated</li>
     *           <li>Current user is not the buyer of the order</li>
     *         </ul>
     */
    OrderDTO updateAddress(UpdateOrderAddressRequest request, String orderId);

    /**
     * Retrieves an order by its ID.
     *
     * @param id the unique identifier of the order
     * @return OrderDTO containing complete order information including items and status history
     * @throws BadRequestException if order with the given ID is not found
     */
    OrderDTO getById(String id);

    /**
     * Cancels an order initiated by the buyer.
     *
     * <p>This operation is only allowed for orders that are:
     * <ul>
     *   <li>In PENDING main status</li>
     *   <li>Have delivery status of PND (pending)</li>
     * </ul>
     *
     * <p>When cancelled, the order main status is set to CANCELED and a new
     * order status entry is created with delivery status CNL (cancelled).
     *
     * @param orderId the ID of the order to cancel
     * @return null (void operation)
     * @throws BadRequestException if:
     *         <ul>
     *           <li>Order with the given ID is not found</li>
     *           <li>Order cannot be cancelled (wrong status)</li>
     *           <li>Order status information is missing</li>
     *         </ul>
     * @throws SecurityException if:
     *         <ul>
     *           <li>User is not authenticated</li>
     *           <li>Current user is not the buyer of the order</li>
     *         </ul>
     */
    Void userCancelOrder(String orderId);

    /**
     * Updates order status by staff members (delivery and/or payment status).
     *
     * <p>This method allows staff to update both delivery and payment statuses
     * with proper validation of status transitions. Common operations include:
     * <ul>
     *   <li>Confirming orders (CF)</li>
     *   <li>Setting delivery status (DLN, DLD)</li>
     *   <li>Processing payments (PD)</li>
     *   <li>Handling returns and refunds (D_RT, R_CF, RFG, RFD)</li>
     * </ul>
     *
     * <p>The method validates status transition rules and updates the main order
     * status accordingly. Some valid combinations include:
     * <ul>
     *   <li>Delivered + Paid: marks COD orders as completed</li>
     *   <li>Cancelled/Declined/Returned + Refunding: initiates refund process</li>
     * </ul>
     *
     * @param orderId the ID of the order to update
     * @param request the status update request containing new delivery and/or payment status
     * @return null (void operation)
     * @throws BadRequestException if:
     *         <ul>
     *           <li>Order with the given ID is not found</li>
     *           <li>Order status information is missing</li>
     *           <li>Neither delivery nor payment status is provided</li>
     *           <li>Invalid status combination is requested</li>
     *           <li>Status transition is not allowed based on current status</li>
     *         </ul>
     * @throws SecurityException if user is not authenticated or lacks staff privileges
     */
    Void staffUpdateOrderStatus(String orderId, UpdateOrderStatusRequest request);

    /**
     * Initiates a return request for a delivered order.
     *
     * <p>This operation is only allowed for orders that have been successfully
     * delivered (delivery status = DLD). Only the buyer can request a return.
     *
     * <p><strong>Note:</strong> Current implementation is incomplete and only
     * validates preconditions without creating the actual return request.
     *
     * @param orderId the ID of the order to return
     * @return null (void operation)
     * @throws BadRequestException if:
     *         <ul>
     *           <li>Order with the given ID is not found</li>
     *           <li>Order status information is missing</li>
     *           <li>Order has not been delivered yet</li>
     *         </ul>
     * @throws SecurityException if:
     *         <ul>
     *           <li>User is not authenticated</li>
     *           <li>Current user is not the buyer of the order</li>
     *         </ul>
     */
    Void userReturnOrder(String orderId);

    Void userConfirmOrderCompleted(String orderId);
}