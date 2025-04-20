package com.LoanManagementApp.LoansApp.Repositories;

import com.LoanManagementApp.LoansApp.Models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}