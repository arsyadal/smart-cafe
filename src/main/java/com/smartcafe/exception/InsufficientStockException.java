package com.smartcafe.exception;

/**
 * InsufficientStockException - Custom Exception for Inventory Management
 * 
 * Thrown when attempting to order more items than available in stock.
 * This is a RuntimeException so it doesn't require explicit handling,
 * but should be caught by the global exception handler for proper API
 * responses.
 */
public class InsufficientStockException extends RuntimeException {

    private final String productName;
    private final int requestedQuantity;
    private final int availableStock;

    /**
     * Constructor with product details
     * 
     * @param productName       the name of the product with insufficient stock
     * @param requestedQuantity the quantity that was requested
     * @param availableStock    the actual available stock
     */
    public InsufficientStockException(String productName, int requestedQuantity, int availableStock) {
        super(String.format("Insufficient stock for '%s': requested %d, available %d",
                productName, requestedQuantity, availableStock));
        this.productName = productName;
        this.requestedQuantity = requestedQuantity;
        this.availableStock = availableStock;
    }

    /**
     * Simple constructor with message only
     */
    public InsufficientStockException(String message) {
        super(message);
        this.productName = "Unknown";
        this.requestedQuantity = 0;
        this.availableStock = 0;
    }

    public String getProductName() {
        return productName;
    }

    public int getRequestedQuantity() {
        return requestedQuantity;
    }

    public int getAvailableStock() {
        return availableStock;
    }
}
