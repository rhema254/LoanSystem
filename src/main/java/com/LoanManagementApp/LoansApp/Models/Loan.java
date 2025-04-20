// src/main/java/com/LoanManagementApp/LoansApp/Models/Loan.java
package com.LoanManagementApp.LoansApp.Models;

import com.LoanManagementApp.LoansApp.Enums.LoanStatus;
import com.LoanManagementApp.LoansApp.Enums.PaymentFrequency;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private Double principalAmount;

    @Column(nullable = false)
    private Double interestRate;

    @Column(nullable = false)
    private Integer repaymentPeriod;

    @Column(nullable = false)
    private Double outstandingBalance; // Remaining amount to be paid

    @Column(nullable = false)
    private Double totalPaid; // Total payments made

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus = LoanStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentFrequency frequency; // The repayment duration.(Monthly, ...etc)

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private LoanProduct product;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_officer_id")
    private User loanOfficer;

}
