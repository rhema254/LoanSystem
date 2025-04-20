package com.LoanManagementApp.LoansApp.Responses;

import lombok.Data;

import java.util.Map;

@Data
public class LoanAnalyticsDTO {
    private Double totalPortfolioValue;
    private Map<String, Long> loansDisbursedVsPaid;
    private Map<String, Long> disbursementTrends;
    private Double onTimeRepaymentRate;
    private Double defaultRate;
    private Map<String, Long> loansByProduct;
    private Map<String, Double> loanOfficerPerformance;
    private Double averageLoanSize;
    private Double interestIncome;
    private Double customerRetention;
}