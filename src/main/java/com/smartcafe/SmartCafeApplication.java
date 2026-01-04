package com.smartcafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Smart Cafe System - Main Application Entry Point
 * 
 * This application demonstrates:
 * - OOP principles with abstract classes and interfaces
 * - Spring Data JPA for database persistence
 * - REST API endpoints for product and order management
 * - WebSocket for real-time kitchen dashboard updates
 * - Thymeleaf with Bootstrap 5 for the frontend
 * 
 * @author Smart Cafe Team
 * @version 1.0.0
 */
@SpringBootApplication
public class SmartCafeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCafeApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Smart Cafe System Started Successfully!");
        System.out.println("  Customer Menu: http://localhost:8080/");
        System.out.println("  Kitchen Dashboard: http://localhost:8080/kitchen");
        System.out.println("========================================\n");
    }
}
