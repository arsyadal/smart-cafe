package com.smartcafe.config;

import com.smartcafe.model.*;
import com.smartcafe.repository.ProductRepository;
import com.smartcafe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * DataInitializer - Seeds the database with sample data
 * 
 * Runs on application startup to create:
 * - Sample users (admin and staff)
 * - Sample products (food and drinks)
 * 
 * This is for demonstration purposes only.
 * In production, you would use database migrations (Flyway/Liquibase).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final ProductRepository productRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) throws Exception {
                // Only seed if database is empty
                if (userRepository.count() == 0) {
                        seedUsers();
                }

                if (productRepository.count() == 0) {
                        seedProducts();
                }

                log.info("Data initialization complete");
        }

        /**
         * Create sample users
         */
        private void seedUsers() {
                log.info("Seeding users...");

                // Admin user
                User admin = User.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("admin123"))
                                .role(UserRole.ADMIN)
                                .fullName("Administrator")
                                .enabled(true)
                                .build();
                userRepository.save(admin);

                // Staff user
                User staff = User.builder()
                                .username("staff")
                                .password(passwordEncoder.encode("staff123"))
                                .role(UserRole.STAFF)
                                .fullName("Cafe Staff")
                                .enabled(true)
                                .build();
                userRepository.save(staff);

                log.info("Created {} users", userRepository.count());
        }

        /**
         * Create sample products (food and drinks)
         */
        private void seedProducts() {
                log.info("Seeding products...");

                // ========== FOOD ITEMS ==========

                Food croissant = new Food(
                                "Butter Croissant",
                                25000.0,
                                50,
                                false,
                                "Freshly baked buttery croissant",
                                "https://images.unsplash.com/photo-1555507036-ab1f4038808a?w=300");
                productRepository.save(croissant);

                Food veggieWrap = new Food(
                                "Veggie Wrap",
                                45000.0,
                                30,
                                true,
                                "Fresh vegetables in a whole wheat wrap",
                                "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=300");
                productRepository.save(veggieWrap);

                Food chickenSandwich = new Food(
                                "Grilled Chicken Sandwich",
                                55000.0,
                                25,
                                false,
                                "Grilled chicken breast with lettuce and tomato",
                                "https://images.unsplash.com/photo-1553909489-cd47e0907980?w=300");
                productRepository.save(chickenSandwich);

                Food caesarSalad = new Food(
                                "Caesar Salad",
                                48000.0,
                                20,
                                true,
                                "Classic Caesar salad with parmesan",
                                "https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=300");
                productRepository.save(caesarSalad);

                Food chocolateCake = new Food(
                                "Chocolate Cake",
                                35000.0,
                                15,
                                true,
                                "Rich chocolate cake slice",
                                "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=300");
                productRepository.save(chocolateCake);

                // ========== DRINK ITEMS ==========

                Drink espresso = new Drink(
                                "Espresso",
                                20000.0,
                                100,
                                false,
                                "Strong Italian espresso shot",
                                "https://images.unsplash.com/photo-1510707577719-ae7c14805e3a?w=300");
                productRepository.save(espresso);

                Drink icedLatte = new Drink(
                                "Iced Caramel Latte",
                                38000.0,
                                75,
                                true,
                                "Cold brew with caramel and milk",
                                "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=300");
                productRepository.save(icedLatte);

                Drink hotChocolate = new Drink(
                                "Hot Chocolate",
                                32000.0,
                                60,
                                false,
                                "Creamy hot chocolate with whipped cream",
                                "https://images.unsplash.com/photo-1542990253-0d0f5be5f0ed?w=300");
                productRepository.save(hotChocolate);

                Drink greenTea = new Drink(
                                "Iced Matcha Latte",
                                42000.0,
                                45,
                                true,
                                "Japanese matcha with oat milk",
                                "https://images.unsplash.com/photo-1515823064-d6e0c04616a7?w=300");
                productRepository.save(greenTea);

                Drink orangeJuice = new Drink(
                                "Fresh Orange Juice",
                                28000.0,
                                40,
                                true,
                                "Freshly squeezed orange juice",
                                "https://images.unsplash.com/photo-1621506289937-a8e4df240d0b?w=300");
                productRepository.save(orangeJuice);

                Drink smoothie = new Drink(
                                "Berry Smoothie",
                                45000.0,
                                35,
                                true,
                                "Mixed berries with yogurt and honey",
                                "https://images.unsplash.com/photo-1553530666-ba11a7da3888?w=300");
                productRepository.save(smoothie);

                log.info("Created {} products", productRepository.count());
        }
}
