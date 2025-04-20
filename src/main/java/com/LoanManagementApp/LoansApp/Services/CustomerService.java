package com.LoanManagementApp.LoansApp.Services;

import com.LoanManagementApp.LoansApp.Models.Customer;
import com.LoanManagementApp.LoansApp.Repositories.CustomerRepository;
import com.LoanManagementApp.LoansApp.Repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;
    @Autowired
    private final LoanRepository loanRepository;

    public CustomerService(CustomerRepository customerRepository, LoanRepository loanRepository) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
    }


    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (customerRepository.existsByIdNumber(customer.getIdNumber())) {
            throw new IllegalArgumentException("ID number already exists");
        }
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        return customerRepository.findById(id)
                .map(existing -> {
                    if (!existing.getEmail().equals(updatedCustomer.getEmail()) &&
                            customerRepository.existsByEmail(updatedCustomer.getEmail())) {
                        throw new IllegalArgumentException("Email already exists");
                    }
                    if (!existing.getIdNumber().equals(updatedCustomer.getIdNumber()) &&
                            customerRepository.existsByIdNumber(updatedCustomer.getIdNumber())) {
                        throw new IllegalArgumentException("ID number already exists");
                    }
                    existing.setFirstName(updatedCustomer.getFirstName());
                    existing.setLastName(updatedCustomer.getLastName());
                    existing.setEmail(updatedCustomer.getEmail());
                    existing.setPhone(updatedCustomer.getPhone());
                    existing.setAddress(updatedCustomer.getAddress());
                    existing.setIdNumber(updatedCustomer.getIdNumber());
                    return customerRepository.save(existing);
                })
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    @Transactional
   public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found with ID: " + id);
        }

        // Check if the customer has any associated loans
        Long loanCount = loanRepository.countByCustomerId(id);
        if (loanCount > 0) {
            throw new IllegalStateException("Cannot delete customer with ID: " + id + " because they have " + loanCount + " associated loan(s)");
        }

        customerRepository.deleteById(id);
        }
}
