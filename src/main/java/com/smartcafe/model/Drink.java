package com.smartcafe.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Drink - Concrete Product Subclass (OOP Focus)
 * 
 * Demonstrates:
 * - Inheritance: Extends abstract Product class
 * - Polymorphism: Implements Discountable interface with drink-specific logic
 * - Single Responsibility: Only contains drink-specific attributes
 * 
 * Drink products have an additional field to indicate if they are served cold.
 */
@Entity
@DiscriminatorValue("DRINK")
@Getter
@Setter
@NoArgsConstructor
public class Drink extends Product implements Discountable {
    
    /**
     * Indicates if this drink is served cold
     * Examples: Iced Coffee (true), Hot Chocolate (false)
     */
    @Column(name = "is_cold")
    private Boolean isCold = false;
    
    /**
     * Current discount percentage for seasonal promotions
     * Range: 0.0 (no discount) to 1.0 (100% off)
     */
    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;
    
    /**
     * Size of the drink (optional)
     * Examples: "Small", "Medium", "Large"
     */
    @Column(name = "size", length = 20)
    private String size;
    
    /**
     * Constructor for creating a new Drink product
     * 
     * @param name product name
     * @param price product price
     * @param stock initial stock quantity
     * @param isCold whether the drink is served cold
     */
    public Drink(String name, Double price, Integer stock, Boolean isCold) {
        super(name, price, stock);
        this.isCold = isCold;
        this.discountPercentage = 0.0;
    }
    
    /**
     * Full constructor including description and image
     */
    public Drink(String name, Double price, Integer stock, Boolean isCold,
                 String description, String imageUrl) {
        super(name, price, stock);
        this.isCold = isCold;
        this.discountPercentage = 0.0;
        setDescription(description);
        setImageUrl(imageUrl);
    }
    
    // ========================================
    // Discountable Interface Implementation
    // ========================================
    
    @Override
    public double getDiscountPercentage() {
        return discountPercentage != null ? discountPercentage : 0.0;
    }
    
    @Override
    public void setDiscountPercentage(double percentage) {
        // Ensure percentage is within valid range
        if (percentage < 0) {
            this.discountPercentage = 0.0;
        } else if (percentage > 1.0) {
            this.discountPercentage = 1.0;
        } else {
            this.discountPercentage = percentage;
        }
    }
    
    @Override
    public double getDiscountedPrice() {
        double discount = getDiscountPercentage();
        return getPrice() * (1 - discount);
    }
    
    @Override
    public String getProductType() {
        return "DRINK";
    }
    
    @Override
    public String toString() {
        return String.format("Drink{id=%d, name='%s', price=%.2f, stock=%d, cold=%s}", 
                getId(), getName(), getPrice(), getStock(), isCold);
    }
}
