package com.smartcafe.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Food - Concrete Product Subclass (OOP Focus)
 * 
 * Demonstrates:
 * - Inheritance: Extends abstract Product class
 * - Polymorphism: Implements Discountable interface differently than Drink
 * - Single Responsibility: Only contains food-specific attributes
 * 
 * Food products have an additional field to indicate if they are vegetarian.
 */
@Entity
@DiscriminatorValue("FOOD")
@Getter
@Setter
@NoArgsConstructor
public class Food extends Product implements Discountable {
    
    /**
     * Indicates if this food item is vegetarian
     */
    @Column(name = "is_vegetarian")
    private Boolean isVegetarian = false;
    
    /**
     * Current discount percentage for seasonal promotions
     * Range: 0.0 (no discount) to 1.0 (100% off)
     */
    @Column(name = "discount_percentage")
    private Double discountPercentage = 0.0;
    
    /**
     * Constructor for creating a new Food product
     * 
     * @param name product name
     * @param price product price
     * @param stock initial stock quantity
     * @param isVegetarian whether the food is vegetarian
     */
    public Food(String name, Double price, Integer stock, Boolean isVegetarian) {
        super(name, price, stock);
        this.isVegetarian = isVegetarian;
        this.discountPercentage = 0.0;
    }
    
    /**
     * Full constructor including description and image
     */
    public Food(String name, Double price, Integer stock, Boolean isVegetarian, 
                String description, String imageUrl) {
        super(name, price, stock);
        this.isVegetarian = isVegetarian;
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
        return "FOOD";
    }
    
    @Override
    public String toString() {
        return String.format("Food{id=%d, name='%s', price=%.2f, stock=%d, vegetarian=%s}", 
                getId(), getName(), getPrice(), getStock(), isVegetarian);
    }
}
