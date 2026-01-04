package com.smartcafe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * OrderItem Entity - Represents a single line item in an order
 * 
 * Relationships:
 * - Many-to-One with Order: Each item belongs to one order
 * - Many-to-One with Product: Each item references one product
 * 
 * This entity stores:
 * - The quantity ordered
 * - The subtotal (price * quantity at time of order)
 * - Special requests for this specific item
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Many-to-One relationship with Order
     * FetchType.LAZY for performance (Order loads separately)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    /**
     * Many-to-One relationship with Product
     * FetchType.EAGER so product details are available with item
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Quantity of this product ordered
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Price per unit at the time of order
     * Stored separately in case product price changes later
     */
    @Column(name = "unit_price", nullable = false)
    private Double unitPrice;

    /**
     * Subtotal for this line item (unitPrice * quantity)
     */
    @Column(nullable = false)
    private Double subtotal;

    /**
     * Optional special requests for this item
     * Example: "no ice", "extra sugar", "well done"
     */
    @Column(name = "special_requests", length = 255)
    private String specialRequests;

    /**
     * Constructor for creating an order item
     * Automatically calculates subtotal
     * 
     * @param product  the product being ordered
     * @param quantity the quantity to order
     */
    public OrderItem(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        calculateSubtotal();
    }

    /**
     * Constructor with special requests
     */
    public OrderItem(Product product, Integer quantity, String specialRequests) {
        this(product, quantity);
        this.specialRequests = specialRequests;
    }

    /**
     * Calculate the subtotal for this item
     * Should be called when quantity or unitPrice changes
     */
    public void calculateSubtotal() {
        if (unitPrice != null && quantity != null) {
            this.subtotal = unitPrice * quantity;
        }
    }

    /**
     * Pre-persist hook to ensure subtotal is calculated
     */
    @PrePersist
    @PreUpdate
    protected void onSave() {
        if (this.unitPrice == null && this.product != null) {
            this.unitPrice = product.getPrice();
        }
        calculateSubtotal();
    }

    /**
     * Get the product name for display purposes
     */
    public String getProductName() {
        return product != null ? product.getName() : "Unknown";
    }
}
