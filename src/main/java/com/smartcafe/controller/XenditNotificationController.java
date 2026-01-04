package com.smartcafe.controller;

import com.smartcafe.model.Order;
import com.smartcafe.model.Payment;
import com.smartcafe.model.PaymentMethod;
import com.smartcafe.model.PaymentStatus;
import com.smartcafe.repository.OrderRepository;
import com.smartcafe.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller to handle Xendit HTTP Notifications (Webhooks)
 */
@RestController
@RequestMapping("/api/payments/xendit-callback")
@RequiredArgsConstructor
@Slf4j
public class XenditNotificationController {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @PostMapping
    public ResponseEntity<String> handleCallback(@RequestBody Map<String, Object> callback) {
        log.info("Received Xendit notification: {}", callback);

        String externalId = (String) callback.get("external_id");
        String status = (String) callback.get("status");

        if (externalId == null || status == null) {
            return ResponseEntity.badRequest().body("Invalid Callback Data");
        }

        // Parse orderId (Format: SC-{id}-{random})
        String[] parts = externalId.split("-");
        if (parts.length < 2) {
            return ResponseEntity.badRequest().body("Invalid External ID format");
        }

        Long orderId;
        try {
            orderId = Long.parseLong(parts[1]);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid Order ID in External ID");
        }

        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            log.error("Order #{} not found for notification", orderId);
            return ResponseEntity.ok("OK");
        }

        if (status.equals("PAID") || status.equals("SETTLED")) {
            updateOrderPaid(order, callback);
        } else if (status.equals("EXPIRED")) {
            updateOrderFailed(order);
        }

        return ResponseEntity.ok("OK");
    }

    private void updateOrderPaid(Order order, Map<String, Object> callback) {
        log.info("Payment settled for Order #{}", order.getId());

        Payment payment = order.getPayment();
        if (payment == null) {
            payment = new Payment();
            payment.setOrder(order);
            payment.setPaymentTime(LocalDateTime.now());
        }

        payment.setStatus(PaymentStatus.COMPLETED);

        Object amountObj = callback.get("amount");
        double amount = amountObj instanceof Number ? ((Number) amountObj).doubleValue() : 0.0;

        payment.setAmount(amount);
        payment.setTransactionId((String) callback.get("id"));
        payment.setMethod(PaymentMethod.QRIS); // Map all Xendit to QRIS/E-Wallet for now
        paymentRepository.save(payment);

        order.setPayment(payment);
        orderRepository.save(order);
    }

    private void updateOrderFailed(Order order) {
        log.warn("Payment failed/expired for Order #{}", order.getId());
        if (order.getPayment() != null) {
            order.getPayment().setStatus(PaymentStatus.FAILED);
            paymentRepository.save(order.getPayment());
        }
    }
}
