package com.LoanManagementApp.LoansApp.Repositories;

import com.LoanManagementApp.LoansApp.Enums.RepaymentScheduleStatus;
import com.LoanManagementApp.LoansApp.Models.Loan;
import com.LoanManagementApp.LoansApp.Models.RepaymentSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepaymentScheduleRepository extends JpaRepository<RepaymentSchedule, Long> {
    List<RepaymentSchedule> findByLoanId(Long loanId);

    Optional<RepaymentSchedule> findFirstByLoanAndStatusOrderByPaymentNumberAsc(Loan loan, RepaymentScheduleStatus status);

    Optional<RepaymentSchedule> findFirstByLoanAndStatusAndPaymentDateBefore(Loan loan, RepaymentScheduleStatus status, LocalDate date);

    Long countByStatus(RepaymentScheduleStatus status);


}