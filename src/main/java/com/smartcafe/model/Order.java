package com.smartcafe.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Entity - Represents a customer order
 * 
 * Relationships:
 * - One-to-Many with OrderItem: An order can have multiple items
 * 
 * The order tracks:
 * - When it was placed (orderTime)
 * - Current status (using OrderStatus enum)
 * - Total amount (calculated from items)
 * - Reference to the customer/table (optional)
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Timestamp when the order was placed
     */
    @Column(name = "order_time", nullable = false)
    private LocalDateTime orderTime;

    /**
     * Current status of the order
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    /**
     * Total amount for the entire order
     * Calculated as sum of all OrderItem subtotals
     */
    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    /**
     * Optional customer name or table number
     */
    @Column(name = "customer_name", length = 100)
    private String customerName;

    /**
     * Optional notes for the order (e.g., "no ice", "extra spicy")
     */
    @Column(length = 500)
    private String notes;

    /**
     * Relationship with Payment
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_id", referencedColumnName = "id")
    private Payment payment;

    /**
     * One-to-Many relationship with OrderItem
     * CascadeType.ALL: Changes to Order cascade to OrderItems
     * orphanRemoval: Remove items from DB when removed from list
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Pre-persist hook to set order time and default status
     */
    @PrePersist
    protected void onCreate() {
        if (this.orderTime == null) {
            this.orderTime = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = OrderStatus.PENDING;
        }
    }

    /**
     * Add an item to this order
     * Sets up the bidirectional relationship
     * 
     * @param item the OrderItem to add
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Remove an item from this order
     * 
     * @param item the OrderItem to remove
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    /**
     * Calculate and update the total amount based on all items
     */
    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .mapToDouble(OrderItem::getSubtotal)
                .sum();
    }

    /**
     * Check if the order can be cancelled
     */
    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.PREPARING;
    }

    /**
     * Get the total number of items in the order
     */
    public int getTotalItemCount() {
        return items.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }
}
