package com.LoanManagementApp.LoansApp.Responses;

import lombok.Data;

@Data
public class OfficerPortfolioDTO {

    private int loansDisbursed;
    private int loansPaid;
    private String metricDescription;
    private int loansPaidPercent;
    private int biggestLoanDisbursed;
}
