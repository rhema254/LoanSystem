package com.LoanManagementApp.LoansApp.Requests;

import com.LoanManagementApp.LoansApp.Enums.LoanStatus;
import com.LoanManagementApp.LoansApp.Enums.PaymentFrequency;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;


import java.time.LocalDate;

@Data
public class CreateLoanRequestDTO {

    private LocalDate startDate;

    @NotNull(message = "Principal amount is required")
    @Positive(message = "Principal amount must be positive")
    private Double principalAmount;

    private Double interestRate;

    @NotNull(message = "Repayment period is required")
    @Min(value = 1, message = "Repayment period must be at least 1")
    private Integer repaymentPeriod;

    @NotNull(message = "Frequency is required")
    private PaymentFrequency frequency;

    private LoanStatus loanStatus = LoanStatus.ACTIVE;

    private Double totalPaid;

    private Double outstandingBalance;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Product ID is required")
    private Long productId;
}
