package com.LoanManagementApp.LoansApp.Repositories;

import com.LoanManagementApp.LoansApp.Enums.LoanStatus;
import com.LoanManagementApp.LoansApp.Models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByCustomerId(Long customerId);

    Long countByCustomerId(Long customerId);

    @Query("SELECT SUM(l.outstandingBalance) FROM Loan l WHERE l.loanStatus = :status")
    Double sumOutstandingBalancesByStatus(@Param("status") LoanStatus status);

    Long countByLoanStatus(LoanStatus loanStatus);

    @Query("SELECT p.name, COUNT(l) FROM Loan l JOIN l.product p GROUP BY p.name")
    List<Object[]> countLoansByProduct();

    Long countByStartDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT SUM(l.principalAmount) FROM Loan l")
    Double sumPrincipalAmounts();

    @Query("SELECT u.username, COUNT(l), SUM(CASE WHEN l.loanStatus = 'DELINQUENT' THEN 1 ELSE 0 END) " +
            "FROM Loan l JOIN l.loanOfficer u GROUP BY u.username")
    List<Object[]> calculateLoanOfficerPerformance();
}