package com.smartcafe.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * ProductRequest - DTO for creating or updating a product
 */
@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @NotBlank(message = "Product type is required (FOOD or DRINK)")
    private String productType;

    private String description;

    private String imageUrl;

    private Boolean available = true;

    // Food-specific
    private Boolean isVegetarian = false;

    // Drink-specific
    private Boolean isCold = false;

    private String size;
}
