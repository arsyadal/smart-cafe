package com.smartcafe.controller;

import com.smartcafe.model.Product;
import com.smartcafe.model.Food;
import com.smartcafe.model.Drink;
import com.smartcafe.dto.ProductRequest;
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
    public ResponseEntity<List<Product>> getAvailableProducts() {
        log.debug("REST request to get available products");
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * GET /api/products/all
     * Returns ALL products (including unavailable ones) for admin
     * 
     * @return list of all products
     */
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProducts() {
        log.debug("REST request to get all products for admin");
        List<Product> products = productService.getAllProducts();
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
     * POST /api/products
     * Creates a new product
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        log.debug("REST request to create product: {}", request.getName());
        Product product;
        if ("FOOD".equalsIgnoreCase(request.getProductType())) {
            Food food = new Food();
            food.setIsVegetarian(request.getIsVegetarian());
            product = food;
        } else {
            Drink drink = new Drink();
            drink.setIsCold(request.getIsCold());
            drink.setSize(request.getSize());
            product = drink;
        }

        mapRequestToProduct(request, product);
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    /**
     * PUT /api/products/{id}
     * Updates an existing product
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
            @RequestBody ProductRequest request) {
        log.debug("REST request to update product: {}", id);
        Product product = productService.getProductById(id);

        if (product instanceof Food && "FOOD".equalsIgnoreCase(request.getProductType())) {
            ((Food) product).setIsVegetarian(request.getIsVegetarian());
        } else if (product instanceof Drink && "DRINK".equalsIgnoreCase(request.getProductType())) {
            ((Drink) product).setIsCold(request.getIsCold());
            ((Drink) product).setSize(request.getSize());
        }

        mapRequestToProduct(request, product);
        return ResponseEntity.ok(productService.saveProduct(product));
    }

    /**
     * DELETE /api/products/{id}
     * Deletes a product
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        log.debug("REST request to delete product: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
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

    private void mapRequestToProduct(ProductRequest request, Product product) {
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setAvailable(request.getAvailable());
    }
}
