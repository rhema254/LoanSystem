package com.LoanManagementApp.LoansApp.Controllers;

import com.LoanManagementApp.LoansApp.Responses.LoanPortfolioDTO;
import com.LoanManagementApp.LoansApp.Responses.OfficerPortfolioDTO;
import com.LoanManagementApp.LoansApp.Services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService){
        this.analyticsService = analyticsService;
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/portfolio")
    public ResponseEntity<LoanPortfolioDTO> getLoansPaidVsDisbursed() {
        LoanPortfolioDTO portfolioDTO = analyticsService.loansPaidVsDisbursed();
        return new ResponseEntity<>(portfolioDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}/portfolio")
    public ResponseEntity<OfficerPortfolioDTO> getOfficerLoanPortfolio(@PathVariable Long id){
        OfficerPortfolioDTO officerPortfolio = analyticsService.getOfficerPortfolio(id);
        return  new ResponseEntity<>(officerPortfolio, HttpStatus.OK);
    }

}
