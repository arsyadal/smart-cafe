package com.smartcafe.model;

/**
 * OrderStatus Enum
 * 
 * Represents the lifecycle states of an order in the cafe system.
 * This enum ensures type-safe status management.
 * 
 * Order Flow:
 * PENDING -> PREPARING -> READY -> COMPLETED
 *        \-> CANCELLED (can be cancelled from PENDING or PREPARING)
 */
public enum OrderStatus {
    
    /**
     * Order has been created but not yet acknowledged by kitchen
     */
    PENDING("Pending", "Order awaiting kitchen acknowledgment"),
    
    /**
     * Kitchen has started preparing the order
     */
    PREPARING("Preparing", "Order is being prepared in kitchen"),
    
    /**
     * Order is ready for pickup/serving
     */
    READY("Ready", "Order is ready for pickup"),
    
    /**
     * Order has been delivered/picked up by customer
     */
    COMPLETED("Completed", "Order has been fulfilled"),
    
    /**
     * Order was cancelled before completion
     */
    CANCELLED("Cancelled", "Order was cancelled");
    
    private final String displayName;
    private final String description;
    
    OrderStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * Check if the order can transition to the target status.
     * 
     * @param targetStatus the desired next status
     * @return true if the transition is valid
     */
    public boolean canTransitionTo(OrderStatus targetStatus) {
        return switch (this) {
            case PENDING -> targetStatus == PREPARING || targetStatus == CANCELLED;
            case PREPARING -> targetStatus == READY || targetStatus == CANCELLED;
            case READY -> targetStatus == COMPLETED;
            case COMPLETED, CANCELLED -> false; // Terminal states
        };
    }
    
    /**
     * Check if this status is a terminal (final) state.
     * 
     * @return true if no further transitions are possible
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
