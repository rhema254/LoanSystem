package com.LoanManagementApp.LoansApp.Repositories;

import com.LoanManagementApp.LoansApp.Models.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
    Optional<LoanProduct> findByName(String name);
    boolean existsByName(String name);
}