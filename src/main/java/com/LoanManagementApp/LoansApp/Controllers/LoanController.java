package com.LoanManagementApp.LoansApp.Controllers;

import com.LoanManagementApp.LoansApp.Models.*;
import com.LoanManagementApp.LoansApp.Requests.CreateLoanRequestDTO;
import com.LoanManagementApp.LoansApp.Responses.LoanDTO;
import com.LoanManagementApp.LoansApp.Responses.LoanResponseDTO;
import com.LoanManagementApp.LoansApp.Services.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    @Autowired
    private final LoanService loanService;

//    public LoanController(LoanService loanService) {
//        this.loanService = loanService;
//    }

//    --------------------- Loan Functions --------------------


    @PreAuthorize("hasRole('LOAN_OFFICER')")
    @PostMapping("/loan")
    public ResponseEntity<Map<String, Object>> createLoan(@Valid @RequestBody CreateLoanRequestDTO createLoanRequestDTO) {
        LoanDTO createdLoanDTO = loanService.convertToDTO(loanService.createLoan(createLoanRequestDTO));

        Map<String, Object> response = new HashMap<>();
        response.put("New Loan", createdLoanDTO);
        response.put("message", "New Loan created successfully!");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

// !!! -------------- Circular Reference in response!! -----------------
    @PreAuthorize("hasRole('LOAN_OFFICER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @GetMapping("/loans")
    public ResponseEntity<List<LoanDTO>> getAllLoans(){
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('LOAN_OFFICER')")
    @GetMapping("/loans/customer/{customerId}")
    public ResponseEntity<List<Loan>> getLoansByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(loanService.getLoansByCustomerId(customerId));
    }



//    ------------------ Loan Product Methods -------------------------

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/newLoanProduct")
    public ResponseEntity<Map<String, Object>> createLoanProduct(@Valid @RequestBody LoanProduct product) {
        LoanProduct created = loanService.createLoanProduct(
                product.getName(),
                product.getDefaultInterestRate(),
                product.getAllowedFrequencies()
        );
        Map<String, Object> response =new HashMap<>();
        response.put("message", "Loan Product Created Successfully");
        response.put("loanProduct", created);
        return new ResponseEntity<>(response,  HttpStatus.CREATED);
    }

    @GetMapping("/loan-products")
    public ResponseEntity<List<LoanProduct>> getAllLoanProducts() {
        return ResponseEntity.ok(loanService.getAllLoanProducts());
    }




//    ------------------- Loan Schedule Functions ------------------


    @PreAuthorize("hasRole('ADMIN') or hasRole('LOAN_OFFICER')")
    @GetMapping("/loans/{id}/schedule")
    public ResponseEntity<List<RepaymentSchedule>> getRepaymentSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.getRepaymentSchedule(id));
    }
}