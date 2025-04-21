package com.LoanManagementApp.LoansApp.Responses;

import jdk.jfr.Percentage;
import lombok.Data;

@Data
public class LoanPortfolioDTO {
    private int loansDisbursed;
    private int loansPaid;
    private String metricDescription;
    private int loansPaidPercent;
}
