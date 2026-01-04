package com.smartcafe.controller;

import com.smartcafe.dto.PaymentRequest;
import com.smartcafe.dto.XenditResponse;
import com.smartcafe.model.Payment;
import com.smartcafe.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * PaymentController - REST API for Payment processing (Xendit)
 */
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * GET /api/payments/xendit-invoice
     * Generates a Xendit Invoice for the given order
     */
    @GetMapping("/xendit-invoice")
    public ResponseEntity<XenditResponse> getXenditInvoice(@RequestParam Long orderId) {
        log.info("REST request to get Xendit Invoice for Order #{}", orderId);
        String url = paymentService.createXenditInvoice(orderId);
        return ResponseEntity.ok(new XenditResponse(url, "SC-" + orderId));
    }

    /**
     * POST /api/payments
     * Process a simulated payment
     */
    @PostMapping
    public ResponseEntity<Payment> processPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("REST request to process payment for Order #{}", request.getOrderId());
        Payment payment = paymentService.processPayment(request);
        return ResponseEntity.ok(payment);
    }
}
