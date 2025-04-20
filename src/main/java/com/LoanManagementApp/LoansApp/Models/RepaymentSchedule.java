// src/main/java/com/LoanManagementApp/LoansApp/Models/RepaymentSchedule.java
package com.LoanManagementApp.LoansApp.Models;

import com.LoanManagementApp.LoansApp.Enums.RepaymentScheduleStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "repayment_schedules")
public class RepaymentSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    @Column(nullable = false)
    private Integer paymentNumber;

    @Column(nullable = false)
    private LocalDate paymentDate;

    @Column(nullable = false)
    private Double paymentAmount;

    @Column(nullable = false)
    private Double principalAmount;

    @Column(nullable = false)
    private Double interestAmount;

    @Column(nullable = false)
    private Double remainingBalance;

    private RepaymentScheduleStatus status = RepaymentScheduleStatus.WAITING;

}