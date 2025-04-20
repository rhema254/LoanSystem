package com.LoanManagementApp.LoansApp.Responses;

import com.LoanManagementApp.LoansApp.Enums.PaymentFrequency;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LoanResponseDTO {
    private Long id;
    private Double principalAmount;
    private Double interestRate;
    private Integer repaymentPeriod;
    private PaymentFrequency frequency;
    private LocalDate startDate;
    private Long customerId;
    private String customerFirstName;
    private String customerLastName;
    private Long productId;
    private String productName;

}
