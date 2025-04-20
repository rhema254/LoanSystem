// src/main/java/com/LoanManagementApp/LoansApp/Models/Payment.java
package com.LoanManagementApp.LoansApp.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private String paymentMethod; // CASH, MOBILE_MONEY, BANK_TRANSFER
    
    private String transactionId; // For future mobile payments (e.g., M-Pesa)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repayment_schedule_id", nullable = true)
    private RepaymentSchedule repaymentSchedule; // Optional link to installment
}
