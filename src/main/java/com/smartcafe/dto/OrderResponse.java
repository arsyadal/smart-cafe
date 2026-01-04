package com.smartcafe.dto;

import com.smartcafe.model.Order;
import com.smartcafe.model.OrderItem;
import com.smartcafe.model.OrderStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderResponse DTO - Used for API responses and WebSocket messages
 * 
 * Clean representation of an order without JPA entity relationships.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long id;
    private LocalDateTime orderTime;
    private OrderStatus status;
    private Double totalAmount;
    private String customerName;
    private String notes;
    private List<OrderItemResponse> items;
    private int totalItemCount;

    /**
     * Create response from Order entity
     * 
     * @param order the order entity
     * @return OrderResponse DTO
     */
    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderTime(order.getOrderTime())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .customerName(order.getCustomerName())
                .notes(order.getNotes())
                .items(order.getItems().stream()
                        .map(OrderItemResponse::fromEntity)
                        .collect(Collectors.toList()))
                .totalItemCount(order.getTotalItemCount())
                .build();
    }

    /**
     * Nested DTO for order items
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productType;
        private Integer quantity;
        private Double unitPrice;
        private Double subtotal;
        private String specialRequests;

        public static OrderItemResponse fromEntity(OrderItem item) {
            return OrderItemResponse.builder()
                    .id(item.getId())
                    .productId(item.getProduct().getId())
                    .productName(item.getProduct().getName())
                    .productType(item.getProduct().getProductType())
                    .quantity(item.getQuantity())
                    .unitPrice(item.getUnitPrice())
                    .subtotal(item.getSubtotal())
                    .specialRequests(item.getSpecialRequests())
                    .build();
        }
    }
}
