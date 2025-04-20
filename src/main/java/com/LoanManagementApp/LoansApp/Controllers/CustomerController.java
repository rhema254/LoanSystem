package com.LoanManagementApp.LoansApp.Controllers;

import com.LoanManagementApp.LoansApp.Models.Customer;
import com.LoanManagementApp.LoansApp.Services.CustomerService;
//import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CustomerController {

    @Autowired
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

//    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    @PostMapping("/customer")
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) { // Add @Value
        Customer created = customerService.createCustomer(customer);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

//    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

//    @PreAuthorize("hasRole('ADMIN') or hasRole('OFFICER')")
    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) { // Add @Valid
        Customer updated = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            // Customer not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) {
            // Customer has associated loans
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }



}