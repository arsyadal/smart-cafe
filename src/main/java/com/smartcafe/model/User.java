package com.smartcafe.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * User Entity - Represents cafe staff members
 * 
 * Supports two roles:
 * - ADMIN: Full system access, can manage products, users, and view reports
 * - STAFF: Can take orders and update order status
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username for login
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Encrypted password (using BCrypt)
     */
    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;

    /**
     * User role for authorization (ADMIN or STAFF)
     */
    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    /**
     * Full display name of the user
     */
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    @Column(name = "full_name", length = 100)
    private String fullName;

    /**
     * Indicates if the user account is active
     */
    @Column(nullable = false)
    private Boolean enabled = true;

    /**
     * Constructor for quick user creation
     */
    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.enabled = true;
    }

    /**
     * Check if user has admin privileges
     */
    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }
}
