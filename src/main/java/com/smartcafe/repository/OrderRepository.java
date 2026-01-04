package com.smartcafe.repository;

import com.smartcafe.model.Order;
import com.smartcafe.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Order Repository - Spring Data JPA
 * 
 * Provides CRUD operations for Order entities.
 * Custom queries for order filtering and reporting.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find orders by status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by status, ordered by order time descending
     */
    List<Order> findByStatusOrderByOrderTimeDesc(OrderStatus status);

    /**
     * Find pending orders (for kitchen display)
     */
    List<Order> findByStatusInOrderByOrderTimeAsc(List<OrderStatus> statuses);

    /**
     * Find orders placed within a date range
     */
    @Query("SELECT o FROM Order o WHERE o.orderTime >= :start AND o.orderTime < :end")
    List<Order> findOrdersBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Calculate total revenue for a date range (completed orders only)
     */
    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.status = 'COMPLETED' AND o.orderTime >= :start AND o.orderTime < :end")
    Double calculateRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * Count orders by status
     */
    Long countByStatus(OrderStatus status);

    /**
     * Find recent orders (for dashboard)
     */
    List<Order> findTop10ByOrderByOrderTimeDesc();

    /**
     * Find all active orders (not completed or cancelled)
     */
    @Query("SELECT o FROM Order o WHERE o.status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY o.orderTime ASC")
    List<Order> findActiveOrders();

    /**
     * Find orders by customer name, ordered by time descending
     */
    List<Order> findByCustomerNameOrderByOrderTimeDesc(String customerName);
}
