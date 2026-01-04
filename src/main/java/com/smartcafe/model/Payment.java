package com.smartcafe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Payment Entity - Stores payment transaction details
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "payment_time")
    private LocalDateTime paymentTime;

    @OneToOne(mappedBy = "payment")
    @JsonIgnore
    private Order order;

    @PrePersist
    protected void onCreate() {
        if (this.paymentTime == null) {
            this.paymentTime = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = PaymentStatus.PENDING;
        }
    }
}
