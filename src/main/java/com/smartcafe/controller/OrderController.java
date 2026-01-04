package com.smartcafe.controller;

import com.smartcafe.dto.OrderRequest;
import com.smartcafe.dto.OrderResponse;
import com.smartcafe.dto.StatusUpdateRequest;
import com.smartcafe.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * OrderController - REST API for Order Management
 * 
 * Endpoints:
 * - POST /api/orders - Create a new order
 * - PATCH /api/orders/{id}/status - Update order status
 * - GET /api/orders/active - Get all active orders
 * - GET /api/orders/{id} - Get order by ID
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow CORS for development
public class OrderController {

    private final OrderService orderService;

    /**
     * POST /api/orders
     * Creates a new order from the request
     * 
     * Request body example:
     * {
     * "customerName": "Table 5",
     * "notes": "Rush order",
     * "items": [
     * { "productId": 1, "quantity": 2, "specialRequests": "no ice" },
     * { "productId": 3, "quantity": 1 }
     * ]
     * }
     * 
     * @param request the order request
     * @return the created order
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        log.info("REST request to create order: {}", request.getCustomerName());
        OrderResponse order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    /**
     * PATCH /api/orders/{id}/status
     * Updates the status of an existing order
     * 
     * Request body example:
     * { "status": "PREPARING" }
     * 
     * @param id      the order ID
     * @param request the status update request
     * @return the updated order
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request) {
        log.info("REST request to update order {} status to: {}", id, request.getStatus());
        OrderResponse order = orderService.updateOrderStatus(id, request.getStatus());
        return ResponseEntity.ok(order);
    }

    /**
     * GET /api/orders/{id}
     * Gets an order by ID
     * 
     * @param id the order ID
     * @return the order
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        log.debug("REST request to get order: {}", id);
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * GET /api/orders/active
     * Gets all active orders (for kitchen display)
     * 
     * @return list of active orders
     */
    @GetMapping("/active")
    public ResponseEntity<List<OrderResponse>> getActiveOrders() {
        log.debug("REST request to get active orders");
        List<OrderResponse> orders = orderService.getActiveOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * GET /api/orders/recent
     * Gets recent orders
     * 
     * @return list of recent orders
     */
    @GetMapping("/recent")
    public ResponseEntity<List<OrderResponse>> getRecentOrders() {
        log.debug("REST request to get recent orders");
        List<OrderResponse> orders = orderService.getRecentOrders();
        return ResponseEntity.ok(orders);
    }
}
