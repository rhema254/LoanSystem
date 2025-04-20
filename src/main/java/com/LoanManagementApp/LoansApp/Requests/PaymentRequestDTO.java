package com.LoanManagementApp.LoansApp.Requests;

import lombok.Data;

@Data
public class PaymentRequestDTO {
    private Long loanId;
    private Double amount;
    private String paymentMethod;
    private String transactionId;
}