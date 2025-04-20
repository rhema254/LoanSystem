// src/main/java/com/LoanManagementApp/LoansApp/Controllers/PaymentController.java
package com.LoanManagementApp.LoansApp.Controllers;

import com.LoanManagementApp.LoansApp.Models.Payment;
import com.LoanManagementApp.LoansApp.Requests.PaymentRequestDTO;
import com.LoanManagementApp.LoansApp.Services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/process")
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('ADMIN')")
    public ResponseEntity<Payment> processPayment(@RequestBody PaymentRequestDTO request) {
        Payment payment = paymentService.processPayment(
                request.getLoanId(),
                request.getAmount(),
                request.getPaymentMethod(),
                request.getTransactionId()
        );
        return ResponseEntity.ok(payment);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleMobilePaymentWebhook(@RequestBody String webhookData) {
        // TODO: Parse webhook, validate transaction, and call processPayment
        return ResponseEntity.ok().build();
    }
}