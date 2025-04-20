package com.LoanManagementApp.LoansApp.Repositories;

import com.LoanManagementApp.LoansApp.Models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository <Customer, Long> {

    boolean existsByEmail(String email);
    boolean existsByIdNumber(String idNumber);

    @Query("SELECT COUNT(DISTINCT c) FROM Customer c JOIN c.loans l GROUP BY c HAVING COUNT(l) > 1")
    Long countCustomersWithMultipleLoans();
}


