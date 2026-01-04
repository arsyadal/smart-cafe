package com.smartcafe.model;

/**
 * UserRole Enum
 * 
 * Defines the authorization levels in the Smart Cafe system.
 */
public enum UserRole {

    /**
     * Administrator with full system access
     * - Manage products (CRUD)
     * - Manage users
     * - View financial reports
     * - All STAFF permissions
     */
    ADMIN,

    /**
     * Regular staff member
     * - View products
     * - Create and manage orders
     * - Update order status
     */
    STAFF
}
