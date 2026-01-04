package com.smartcafe.service;

import com.xendit.model.Invoice;
import com.smartcafe.dto.PaymentRequest;
import com.smartcafe.exception.ResourceNotFoundException;
import com.smartcafe.model.*;
import com.smartcafe.repository.OrderRepository;
import com.smartcafe.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * PaymentService - Handles payment processing (Xendit Integrated)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    /**
     * Create Xendit Invoice for an order
     * 
     * @param orderId the order ID
     * @return Xendit Invoice URL
     */
    public String createXenditInvoice(Long orderId) {
        log.info("Creating Xendit Invoice for Order #{}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        // Prepare Xendit request body
        Map<String, Object> params = new HashMap<>();
        params.put("external_id", "SC-" + order.getId() + "-" + UUID.randomUUID().toString().substring(0, 5));
        params.put("amount", order.getTotalAmount().intValue());
        params.put("payer_email", "customer@smartcafe.com");
        params.put("description", "Payment for Smart Cafe Order #" + order.getId());
        params.put("should_send_email", true);

        try {
            Invoice invoice = Invoice.create(params);
            return invoice.getInvoiceUrl();
        } catch (Exception e) {
            log.error("Xendit API Error: {}", e.getMessage(), e);
            throw new RuntimeException("Xendit Error: " + e.getMessage());
        }
    }

    /**
     * Process a payment for an order (Simulated/Internal)
     */
    public Payment processPayment(PaymentRequest request) {
        log.info("Processing internal payment for Order #{} using {}", request.getOrderId(), request.getMethod());

        if (request.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null");
        }
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", request.getOrderId()));

        Payment payment = Payment.builder()
                .amount(request.getAmount())
                .method(request.getMethod())
                .status(PaymentStatus.COMPLETED)
                .transactionId("TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .paymentTime(LocalDateTime.now())
                .order(order)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        order.setPayment(savedPayment);
        orderRepository.save(order);

        return savedPayment;
    }
}
