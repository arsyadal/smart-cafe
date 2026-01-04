package com.smartcafe.service;

import com.smartcafe.dto.OrderRequest;
import com.smartcafe.dto.OrderResponse;
import com.smartcafe.exception.ResourceNotFoundException;
import com.smartcafe.model.*;
import com.smartcafe.repository.OrderRepository;
import com.smartcafe.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderService - Business Logic for Order Management
 * 
 * Handles:
 * - Order creation with stock validation
 * - Order status updates
 * - WebSocket notifications to kitchen
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket destination for kitchen notifications
    private static final String KITCHEN_TOPIC = "/topic/kitchen";

    /**
     * Create a new order from the request
     * 
     * Steps:
     * 1. Validate products exist and have sufficient stock
     * 2. Create order with items
     * 3. Decrease product stock
     * 4. Save order to database
     * 5. Broadcast to kitchen via WebSocket
     * 
     * @param request the order request DTO
     * @return the created order response
     */
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating new order for customer: {}", request.getCustomerName());

        // Create the order entity
        Order order = Order.builder()
                .orderTime(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .customerName(request.getCustomerName())
                .notes(request.getNotes())
                .totalAmount(0.0)
                .build();

        // Process each item in the request
        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            // Get the product
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", itemRequest.getProductId()));

            // Validate and decrease stock
            productService.decreaseStock(product.getId(), itemRequest.getQuantity());

            // Create order item
            OrderItem orderItem = new OrderItem(product, itemRequest.getQuantity());
            orderItem.setSpecialRequests(itemRequest.getSpecialRequests());

            order.addItem(orderItem);
        }

        // Calculate total amount
        order.calculateTotalAmount();

        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order #{} created successfully. Total: ${}", savedOrder.getId(), savedOrder.getTotalAmount());

        // Convert to response DTO
        OrderResponse response = OrderResponse.fromEntity(savedOrder);

        // Broadcast to kitchen dashboard via WebSocket
        broadcastToKitchen(response);

        return response;
    }

    /**
     * Update the status of an order
     * 
     * @param orderId   the order ID
     * @param newStatus the new status
     * @return the updated order response
     */
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        OrderStatus currentStatus = order.getStatus();

        // Validate the status transition
        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new IllegalArgumentException(
                    String.format("Cannot transition order from %s to %s", currentStatus, newStatus));
        }

        // Handle cancellation - restore stock
        if (newStatus == OrderStatus.CANCELLED && order.canBeCancelled()) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order #{} status updated: {} -> {}", orderId, currentStatus, newStatus);

        // Broadcast update to kitchen
        OrderResponse response = OrderResponse.fromEntity(updatedOrder);
        broadcastToKitchen(response);

        return response;
    }

    /**
     * Get an order by ID
     * 
     * @param orderId the order ID
     * @return the order response
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return OrderResponse.fromEntity(order);
    }

    /**
     * Get all active orders (for kitchen display)
     * 
     * @return list of active order responses
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders() {
        return orderRepository.findActiveOrders().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get recent orders
     * 
     * @return list of recent order responses
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getRecentOrders() {
        return orderRepository.findTop10ByOrderByOrderTimeDesc().stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Get orders for a specific customer
     * 
     * @param customerName the customer name
     * @return list of order responses
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCustomer(String customerName) {
        return orderRepository.findByCustomerNameOrderByOrderTimeDesc(customerName).stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Broadcast order to kitchen dashboard via WebSocket
     * 
     * @param orderResponse the order to broadcast
     */
    private void broadcastToKitchen(OrderResponse orderResponse) {
        log.debug("Broadcasting order #{} to kitchen dashboard", orderResponse.getId());
        messagingTemplate.convertAndSend(KITCHEN_TOPIC, orderResponse);
    }

    /**
     * Restore stock when an order is cancelled
     * 
     * @param order the cancelled order
     */
    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            productService.increaseStock(item.getProduct().getId(), item.getQuantity());
        }
        log.info("Stock restored for cancelled order #{}", order.getId());
    }
}
