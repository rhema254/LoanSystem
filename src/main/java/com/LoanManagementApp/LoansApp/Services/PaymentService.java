package com.LoanManagementApp.LoansApp.Services;

import com.LoanManagementApp.LoansApp.Enums.LoanStatus;
import com.LoanManagementApp.LoansApp.Enums.RepaymentScheduleStatus;
import com.LoanManagementApp.LoansApp.Models.Loan;
import com.LoanManagementApp.LoansApp.Models.Payment;
import com.LoanManagementApp.LoansApp.Models.RepaymentSchedule;
import com.LoanManagementApp.LoansApp.Repositories.LoanRepository;
import com.LoanManagementApp.LoansApp.Repositories.PaymentRepository;
import com.LoanManagementApp.LoansApp.Repositories.RepaymentScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private RepaymentScheduleRepository repaymentScheduleRepository;

    @Transactional
    public Payment processPayment(Long loanId, Double amount, String paymentMethod, String transactionId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        // Record payment
        Payment payment = new Payment();
        payment.setLoan(loan);
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDate.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(transactionId);
        paymentRepository.save(payment);

        // Update loan balances
        loan.setTotalPaid(loan.getTotalPaid() == null ? amount : loan.getTotalPaid() + amount);
        loan.setOutstandingBalance(loan.getOutstandingBalance() - amount);

        // Reconcile with repayment schedule
        Optional<RepaymentSchedule> nextInstallment = repaymentScheduleRepository
                .findFirstByLoanAndStatusOrderByPaymentNumberAsc(loan, RepaymentScheduleStatus.WAITING);

        if (nextInstallment.isPresent()) {
            RepaymentSchedule schedule = nextInstallment.get();
            Double remainingDue = schedule.getPaymentAmount();
            if (amount >= remainingDue) {
                schedule.setStatus(RepaymentScheduleStatus.PAID);
                payment.setRepaymentSchedule(schedule);
                if (amount > remainingDue) {
                    reconcileOverpayment(loan, amount - remainingDue);
                }
            } else {
                schedule.setStatus(RepaymentScheduleStatus.PARTIALLY_PAID);
                payment.setRepaymentSchedule(schedule);
            }
            repaymentScheduleRepository.save(schedule);
        } else {
            reconcileOverpayment(loan, amount);
        }

        // Update loan status
        updateLoanStatus(loan);
        loanRepository.save(loan);

        return payment;
    }

    private void reconcileOverpayment(Loan loan, Double excessAmount) {
        Optional<RepaymentSchedule> nextInstallment = repaymentScheduleRepository
                .findFirstByLoanAndStatusOrderByPaymentNumberAsc(loan, RepaymentScheduleStatus.WAITING);
        if (nextInstallment.isPresent()) {
            RepaymentSchedule schedule = nextInstallment.get();
            Double remainingDue = schedule.getPaymentAmount();
            if (excessAmount >= remainingDue) {
                schedule.setStatus(RepaymentScheduleStatus.PAID);
                repaymentScheduleRepository.save(schedule);
                reconcileOverpayment(loan, excessAmount - remainingDue);
            } else {
                schedule.setStatus(RepaymentScheduleStatus.PARTIALLY_PAID);
                repaymentScheduleRepository.save(schedule);
            }
        } else {
            loan.setOutstandingBalance(loan.getOutstandingBalance() - excessAmount);
        }
    }

    private void updateLoanStatus(Loan loan) {
        if (loan.getOutstandingBalance() <= 0) {
            loan.setLoanStatus(LoanStatus.FULLY_SERVICED);
        } else if (loan.getOutstandingBalance() <= loan.getPrincipalAmount()) {
            loan.setLoanStatus(LoanStatus.ACTIVE);
        }

        Optional<RepaymentSchedule> overdue = repaymentScheduleRepository
                .findFirstByLoanAndStatusAndPaymentDateBefore(loan, RepaymentScheduleStatus.WAITING, LocalDate.now());

        if (overdue.isPresent() && loan.getLoanStatus() == LoanStatus.ACTIVE) {
            loan.setLoanStatus(LoanStatus.IN_DEFAULT);
        }
    }
}