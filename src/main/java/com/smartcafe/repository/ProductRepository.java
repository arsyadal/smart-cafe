package com.smartcafe.repository;

import com.smartcafe.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product Repository - Spring Data JPA
 * 
 * Provides CRUD operations for Product entities (Food and Drink).
 * Custom queries for filtering and searching products.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all available products
     */
    List<Product> findByAvailableTrue();

    /**
     * Find products by name containing (case-insensitive search)
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find products with low stock (for inventory alerts)
     */
    @Query("SELECT p FROM Product p WHERE p.stock <= :threshold AND p.available = true")
    List<Product> findLowStockProducts(int threshold);

    /**
     * Find products by type (FOOD or DRINK)
     */
    @Query("SELECT p FROM Product p WHERE TYPE(p) = :productClass")
    List<Product> findByProductType(Class<? extends Product> productClass);

    /**
     * Find available products sorted by name
     */
    List<Product> findByAvailableTrueOrderByNameAsc();
}
