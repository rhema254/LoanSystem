package com.LoanManagementApp.LoansApp.Controllers;

import com.LoanManagementApp.LoansApp.Models.Customer;
import com.LoanManagementApp.LoansApp.Requests.CustomerRequestDTO;
import com.LoanManagementApp.LoansApp.Responses.CustomerResponseDTO;
import com.LoanManagementApp.LoansApp.Services.CustomerService;
//import jakarta.validation.Valid;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }


    private CustomerResponseDTO convertToResponseDTO(Customer customer) {
        CustomerResponseDTO customerDTO = new CustomerResponseDTO();
        customerDTO.setId(customer.getId());
        customerDTO.setFirstName(customer.getFirstName());
        customerDTO.setLastName(customer.getLastName());
        customerDTO.setEmail(customer.getEmail());
        customerDTO.setPhone(customer.getPhone());
        customerDTO.setAddress(customer.getAddress());
        customerDTO.setIdNumber(customer.getIdNumber());
        customerDTO.setDob(customer.getDob());
        customerDTO.setAccountNumber(customer.getAccountNumber());
        return customerDTO;
    }

//-------- Create Customer -----------

    @PreAuthorize("hasRole('ADMIN') or hasRole('LOAN_OFFICER')")
    @PostMapping("/")
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody CustomerRequestDTO customerRequestDTO) { // Add @Value
        Customer created = new Customer();

        created.setFirstName(customerRequestDTO.getFirstName());
        created.setLastName(customerRequestDTO.getLastName());
        created.setEmail(customerRequestDTO.getEmail());
        created.setPhone(customerRequestDTO.getPhone());
        created.setAddress(customerRequestDTO.getAddress());
        created.setIdNumber(customerRequestDTO.getIdNumber());
        created.setDob(customerRequestDTO.getDob());


        Customer createdCustomer = customerService.createCustomer(created);

        return new ResponseEntity<>(convertToResponseDTO(createdCustomer), HttpStatus.CREATED);
    }

//------- Get All Customers -----------

    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN')")
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerResponseDTO>> getAllCustomers() {

        List<Customer> customers = customerService.getAllCustomers();

        List<CustomerResponseDTO> customerResponseDTOs = customers.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(customerResponseDTOs);
    }

//------ Get A User --------

    @PreAuthorize("hasRole('ADMIN') or hasRole('LOAN_OFFICER')")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {

        Optional<Customer> customerOptional = customerService.getCustomerById(id);

        return customerOptional.map(this::convertToResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


// ----- Update A Customer -----

    @PreAuthorize("hasRole('ADMIN') ")
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {

        Customer customer = new Customer();
        customer.setFirstName(customerRequestDTO.getFirstName());
        customer.setLastName(customerRequestDTO.getLastName());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setPhone(customerRequestDTO.getPhone());
        customer.setAddress(customerRequestDTO.getAddress());
        customer.setIdNumber(customerRequestDTO.getIdNumber());


        Customer updatedCustomer = customerService.updateCustomer(id, customer);

        return ResponseEntity.ok(convertToResponseDTO(updatedCustomer));
    }


        // ------- Delete Customer -------
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {

        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }
}


