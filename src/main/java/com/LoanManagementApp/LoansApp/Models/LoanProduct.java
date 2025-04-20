package com.LoanManagementApp.LoansApp.Models;

import com.LoanManagementApp.LoansApp.Enums.PaymentFrequency;
import jakarta.persistence.*;
import lombok.Data;
import java.util.Set;

@Entity
@Data
@Table(name = "loan_products")
public class LoanProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;


    @Column(nullable = false)
    private Double defaultInterestRate;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "loan_product_frequencies", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "payment_frequency")
    private Set<PaymentFrequency> allowedFrequencies;

}
