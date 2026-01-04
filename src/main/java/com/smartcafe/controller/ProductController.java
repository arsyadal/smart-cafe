package com.smartcafe.controller;

import com.smartcafe.model.Product;
import com.smartcafe.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProductController - REST API for Product Management
 * 
 * Endpoints:
 * - GET /api/products - List all available products
 * - GET /api/products/{id} - Get product by ID
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow CORS for development
public class ProductController {

    private final ProductService productService;

    /**
     * GET /api/products
     * Returns all available products for the customer menu
     * 
     * @return list of available products
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.debug("REST request to get all products");
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/{id}
     * Returns a specific product by ID
     * 
     * @param id the product ID
     * @return the product
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        log.debug("REST request to get product: {}", id);
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * GET /api/products/low-stock
     * Returns products with stock below threshold (for admin)
     * 
     * @param threshold minimum stock level (default: 10)
     * @return list of low-stock products
     */
    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        log.debug("REST request to get low stock products (threshold: {})", threshold);
        List<Product> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(products);
    }
}
