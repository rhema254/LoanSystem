package com.LoanManagementApp.LoansApp.Services;

import com.LoanManagementApp.LoansApp.Models.Customer;
import com.LoanManagementApp.LoansApp.Repositories.CustomerRepository;
import com.LoanManagementApp.LoansApp.Repositories.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;
    @Autowired
    private final LoanRepository loanRepository;

    private final Random random = new Random();

    public CustomerService(CustomerRepository customerRepository, LoanRepository loanRepository) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
    }

    private String generateAccountNumber() {
        int randomNumber = random.nextInt(1000); // Generates a random number between 0 and 999
        return String.format("ABC00%03d", randomNumber); // Formats it as "ABC00XXX"
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (customerRepository.existsByIdNumber(customer.getIdNumber())) {
            throw new IllegalArgumentException("ID number already exists");
        }
        String accountNumber;
        do {
            accountNumber = generateAccountNumber();
        } while (customerRepository.existsByAccountNumber(accountNumber)); // Ensure uniqueness

        customer.setAccountNumber(accountNumber);

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
                    if (updatedCustomer.getDob() != null){
                        throw new IllegalArgumentException("Unauthorised Action. Kindly visit your nearest Bank to update it.");
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
