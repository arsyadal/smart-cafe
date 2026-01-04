package com.smartcafe.service;

import com.smartcafe.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * RevenueService - Business Logic for Financial Reporting
 * 
 * Handles:
 * - Daily revenue calculation
 * - Revenue reports by date range
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class RevenueService {

    private final OrderRepository orderRepository;

    /**
     * Calculate total revenue for today
     * Only counts COMPLETED orders
     * 
     * @return today's total revenue
     */
    public Double getTodayRevenue() {
        return getDailyRevenue(LocalDate.now());
    }

    /**
     * Calculate total revenue for a specific date
     * Only counts COMPLETED orders
     * 
     * @param date the date to calculate revenue for
     * @return total revenue for the given date
     */
    public Double getDailyRevenue(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        Double revenue = orderRepository.calculateRevenueBetween(startOfDay, endOfDay);
        log.info("Daily revenue for {}: ${}", date, revenue);

        return revenue != null ? revenue : 0.0;
    }

    /**
     * Calculate total revenue for a date range
     * 
     * @param startDate the start date (inclusive)
     * @param endDate   the end date (inclusive)
     * @return total revenue for the date range
     */
    public Double getRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        Double revenue = orderRepository.calculateRevenueBetween(start, end);
        log.info("Revenue from {} to {}: ${}", startDate, endDate, revenue);

        return revenue != null ? revenue : 0.0;
    }
}
