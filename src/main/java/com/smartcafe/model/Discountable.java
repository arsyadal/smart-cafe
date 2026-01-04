package com.smartcafe.model;

/**
 * Discountable Interface - OOP Focus
 * 
 * This interface demonstrates the Interface Segregation Principle (ISP).
 * Products that can have seasonal discounts should implement this interface.
 * 
 * Benefits:
 * - Allows flexible discount logic per product type
 * - Enables polymorphic discount calculations
 * - Separates discount concerns from base product functionality
 */
public interface Discountable {
    
    /**
     * Get the current discount percentage for this product.
     * 
     * @return discount percentage (e.g., 0.10 for 10% discount)
     */
    double getDiscountPercentage();
    
    /**
     * Set the discount percentage for this product.
     * 
     * @param percentage the discount percentage (0.0 to 1.0)
     */
    void setDiscountPercentage(double percentage);
    
    /**
     * Calculate the discounted price based on the current discount percentage.
     * 
     * @return the final price after applying the discount
     */
    double getDiscountedPrice();
    
    /**
     * Check if this product currently has an active discount.
     * 
     * @return true if discount percentage > 0
     */
    default boolean hasActiveDiscount() {
        return getDiscountPercentage() > 0;
    }
}
