package com.smartcafe.controller;

import com.smartcafe.service.OrderService;
import com.smartcafe.service.ProductService;
import com.smartcafe.service.RevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * WebController - Thymeleaf Page Routes
 * 
 * Handles navigation to frontend pages:
 * - Customer menu page (/)
 * - Kitchen dashboard (/kitchen)
 * - Admin dashboard (/admin)
 */
@Controller
@RequiredArgsConstructor
public class WebController {

    private final ProductService productService;
    private final OrderService orderService;
    private final RevenueService revenueService;

    /**
     * GET /
     * Customer menu page - displays products for ordering
     */
    @GetMapping("/")
    public String customerMenu(Model model) {
        model.addAttribute("products", productService.getAvailableProducts());
        return "index";
    }

    /**
     * GET /kitchen
     * Kitchen dashboard - real-time order display
     */
    @GetMapping("/kitchen")
    public String kitchenDashboard(Model model) {
        model.addAttribute("activeOrders", orderService.getActiveOrders());
        return "kitchen";
    }

    /**
     * GET /admin
     * Admin dashboard - overview and management
     */
    @GetMapping("/admin")
    public String adminDashboard(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("recentOrders", orderService.getRecentOrders());
        model.addAttribute("todayRevenue", revenueService.getTodayRevenue());
        model.addAttribute("lowStockProducts", productService.getLowStockProducts(10));
        return "admin";
    }

    /**
     * GET /login
     * Login page for user authentication
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}
