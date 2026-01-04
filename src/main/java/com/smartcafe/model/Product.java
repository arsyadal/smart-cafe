package com.smartcafe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Product - Abstract Base Class (OOP Focus)
 * 
 * This abstract class demonstrates:
 * - Abstraction: Cannot instantiate directly, must use Food or Drink
 * - Encapsulation: Private fields with public getters/setters via Lombok
 * - Inheritance: Subclasses (Food, Drink) extend this class
 * 
 * JPA Strategy: SINGLE_TABLE inheritance
 * - All product types stored in one table with a discriminator column
 * - Pros: Simple queries, good performance
 * - Cons: Nullable columns for subclass-specific fields
 */
@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "product_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Product name - must be unique and not blank
     */
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * Product price - must be positive
     */
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    @Column(nullable = false)
    private Double price;
    
    /**
     * Current stock quantity - cannot be negative
     */
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    private Integer stock;
    
    /**
     * Optional product description
     */
    @Column(length = 500)
    private String description;
    
    /**
     * URL to product image (optional)
     */
    @Column(name = "image_url", length = 255)
    private String imageUrl;
    
    /**
     * Indicates if the product is currently available for ordering
     */
    @Column(nullable = false)
    private Boolean available = true;
    
    /**
     * Constructor for creating a new product
     */
    public Product(String name, Double price, Integer stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.available = true;
    }
    
    /**
     * Decrease stock by the specified quantity.
     * This method should be called when an order is placed.
     * 
     * @param quantity the quantity to decrease
     * @return true if stock was successfully decreased
     * @throws IllegalArgumentException if quantity is greater than available stock
     */
    public boolean decreaseStock(int quantity) {
        if (quantity > this.stock) {
            return false;
        }
        this.stock -= quantity;
        return true;
    }
    
    /**
     * Increase stock by the specified quantity.
     * Useful for restocking or order cancellations.
     * 
     * @param quantity the quantity to add
     */
    public void increaseStock(int quantity) {
        if (quantity > 0) {
            this.stock += quantity;
        }
    }
    
    /**
     * Check if the product has sufficient stock for the given quantity.
     * 
     * @param quantity the required quantity
     * @return true if stock is sufficient
     */
    public boolean hasStock(int quantity) {
        return this.stock >= quantity;
    }
    
    /**
     * Get the product type (discriminator value).
     * Must be implemented by subclasses.
     * 
     * @return the product type string
     */
    public abstract String getProductType();
}
