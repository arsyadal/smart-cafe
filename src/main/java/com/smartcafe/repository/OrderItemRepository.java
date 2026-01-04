package com.smartcafe.repository;

import com.smartcafe.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * OrderItem Repository - Spring Data JPA
 * 
 * Provides CRUD operations for OrderItem entities.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find all items for a specific order
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Find items by product ID (useful for impact analysis)
     */
    List<OrderItem> findByProductId(Long productId);
}
