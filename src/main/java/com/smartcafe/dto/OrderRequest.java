package com.smartcafe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

/**
 * OrderRequest DTO - Used for creating new orders via REST API
 * 
 * Contains a list of order items with product IDs and quantities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    /**
     * Optional customer name or table number
     */
    private String customerName;

    /**
     * Optional notes for the entire order
     */
    private String notes;

    /**
     * List of items to order
     */
    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequest> items;

    /**
     * Nested DTO for individual order items
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {

        /**
         * Product ID to order
         */
        private Long productId;

        /**
         * Quantity to order
         */
        private Integer quantity;

        /**
         * Optional special requests for this item
         */
        private String specialRequests;
    }
}
