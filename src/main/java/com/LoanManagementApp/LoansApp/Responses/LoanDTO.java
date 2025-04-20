package com.LoanManagementApp.LoansApp.Responses;

import com.LoanManagementApp.LoansApp.Enums.LoanStatus;
import com.LoanManagementApp.LoansApp.Models.User;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;
    private Double principalAmount;
    private Double interestRate;
    private Integer repaymentPeriod;
    private String frequency;
    private LocalDate startDate;
    private CustomerDTO customer; // Reference to CustomerDTO
    private Long productId; // Or a LoanProductDTO if needed
    private User loan_officer;
    private LoanStatus loanStatus;
}
