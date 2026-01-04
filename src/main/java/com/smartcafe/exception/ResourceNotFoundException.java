package com.smartcafe.exception;

/**
 * ResourceNotFoundException - Custom Exception for Missing Entities
 * 
 * Thrown when a requested resource (Product, Order, User) is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceType;
    private final Object resourceId;

    /**
     * Constructor with resource details
     * 
     * @param resourceType the type of resource (e.g., "Product", "Order")
     * @param resourceId   the ID that was not found
     */
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s not found with id: %s", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Simple constructor with message only
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceType = "Resource";
        this.resourceId = null;
    }

    public String getResourceType() {
        return resourceType;
    }

    public Object getResourceId() {
        return resourceId;
    }
}
