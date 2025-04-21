package com.LoanManagementApp.LoansApp.Services;

import com.LoanManagementApp.LoansApp.Enums.LoanStatus;
import com.LoanManagementApp.LoansApp.Models.Loan;
import com.LoanManagementApp.LoansApp.Models.User;
import com.LoanManagementApp.LoansApp.Repositories.LoanRepository;
import com.LoanManagementApp.LoansApp.Repositories.UserRepository;
import com.LoanManagementApp.LoansApp.Responses.LoanPortfolioDTO;
import com.LoanManagementApp.LoansApp.Responses.OfficerPortfolioDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    @Autowired
    private final LoanRepository loanRepository;
    @Autowired
    private final UserRepository userRepository;

    public AnalyticsService(LoanRepository loanRepository, UserRepository userRepository){
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;

    }

    public LoanPortfolioDTO loansPaidVsDisbursed(){
        int loansDisbursed = loanRepository.findAll().size();
        int loansPaid = Math.toIntExact(loanRepository.countByLoanStatus(LoanStatus.FULLY_SERVICED));

        LoanPortfolioDTO loanPortfolioDTO = new LoanPortfolioDTO();
        loanPortfolioDTO.setLoansDisbursed(loansDisbursed);
        loanPortfolioDTO.setLoansPaid(loansPaid);

        if (loansDisbursed < 1){
            loanPortfolioDTO.setLoansPaidPercent(0); // Avoid division by zero
            loanPortfolioDTO.setMetricDescription("You haven't disbursed any Loans!");
            return loanPortfolioDTO;
        }

        double loansPaidPercent = ((double)loansPaid / loansDisbursed) * 100;
        loanPortfolioDTO.setLoansPaidPercent((int) loansPaidPercent); // Cast to int for percentage
        loanPortfolioDTO.setMetricDescription(String.format("Out of the %d loans disbursed, %d%% of them are fully serviced.", loansDisbursed, (int)loansPaidPercent));

        String successMessage = "Analytics Fetch Successful!";
        return loanPortfolioDTO;
    }

    public OfficerPortfolioDTO getOfficerPortfolio(Long officerId) {
        User loanOfficer = userRepository.findById(officerId)
                .orElseThrow(() -> new EntityNotFoundException("Loan Officer not found with ID: " + officerId));

        List<Loan> disbursedLoans = loanRepository.findByLoanOfficerId(officerId);
        int loansDisbursedCount = disbursedLoans.size();
        int loansPaidCount = (int) disbursedLoans.stream()
                .filter(loan -> loan.getLoanStatus() == LoanStatus.FULLY_SERVICED)
                .count();

        double paidPercentage ;
        if (loansDisbursedCount > 0) {
            paidPercentage = ((double) loansPaidCount / loansDisbursedCount) * 100;
        }else{
            paidPercentage = 0.0;
        }

        Double biggestLoan = disbursedLoans.stream()
                .map(loan -> loan.getPrincipalAmount() == null ? 0.0 : loan.getPrincipalAmount())
                .max(Double::compare)
                .orElse(0.0);

        OfficerPortfolioDTO portfolioDTO = new OfficerPortfolioDTO();
        portfolioDTO.setLoansDisbursed(loansDisbursedCount);
        portfolioDTO.setLoansPaid(loansPaidCount);
        portfolioDTO.setLoansPaidPercent((int) paidPercentage);
        portfolioDTO.setBiggestLoanDisbursed(biggestLoan.intValue()); // Assuming you want an integer in DTO
        portfolioDTO.setMetricDescription(String.format("Loan Officer %s has disbursed %d loans, with %d (%.2f%%) fully serviced. The biggest loan disbursed was %d.",
                loanOfficer.getUsername(), loansDisbursedCount, loansPaidCount, paidPercentage, biggestLoan.intValue()));

        return portfolioDTO;
    }

}
