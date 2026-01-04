package com.smartcafe.service;

import com.smartcafe.exception.InsufficientStockException;
import com.smartcafe.exception.ResourceNotFoundException;
import com.smartcafe.model.Product;
import com.smartcafe.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ProductService - Business Logic for Product Management
 * 
 * Handles:
 * - Product CRUD operations
 * - Inventory management (stock increase/decrease)
 * - Stock validation
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Get all products (including unavailable ones)
     * 
     * @return list of all products
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        return productRepository.findAll();
    }

    /**
     * Get only available products for customer display
     * 
     * @return list of available products
     */
    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        log.debug("Fetching available products");
        return productRepository.findByAvailableTrue();
    }

    /**
     * Get a product by ID
     * 
     * @param id the product ID
     * @return the product
     * @throws ResourceNotFoundException if product not found
     */
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
    }

    /**
     * Save or update a product
     * 
     * @param product the product to save
     * @return the saved product
     */
    public Product saveProduct(Product product) {
        log.info("Saving product: {}", product.getName());
        return productRepository.save(product);
    }

    /**
     * Decrease stock for a product
     * Validates that sufficient stock is available
     * 
     * @param productId the product ID
     * @param quantity  the quantity to decrease
     * @throws InsufficientStockException if not enough stock
     */
    public void decreaseStock(Long productId, int quantity) {
        Product product = getProductById(productId);

        if (!product.hasStock(quantity)) {
            log.warn("Insufficient stock for product '{}': requested {}, available {}",
                    product.getName(), quantity, product.getStock());
            throw new InsufficientStockException(
                    product.getName(),
                    quantity,
                    product.getStock());
        }

        product.decreaseStock(quantity);
        productRepository.save(product);
        log.info("Decreased stock for '{}' by {}. New stock: {}",
                product.getName(), quantity, product.getStock());
    }

    /**
     * Increase stock for a product (restock or order cancellation)
     * 
     * @param productId the product ID
     * @param quantity  the quantity to add
     */
    public void increaseStock(Long productId, int quantity) {
        Product product = getProductById(productId);
        product.increaseStock(quantity);
        productRepository.save(product);
        log.info("Increased stock for '{}' by {}. New stock: {}",
                product.getName(), quantity, product.getStock());
    }

    /**
     * Get products with low stock (for inventory alerts)
     * 
     * @param threshold the minimum stock threshold
     * @return list of low-stock products
     */
    @Transactional(readOnly = true)
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStockProducts(threshold);
    }

    /**
     * Delete a product by ID
     * 
     * @param id the product ID
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product", id);
        }
        productRepository.deleteById(id);
        log.info("Deleted product with ID: {}", id);
    }
}
