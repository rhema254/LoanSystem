package com.LoanManagementApp.LoansApp.Responses;

import jakarta.persistence.Column;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String idNumber;
    private LocalDate dob;
    private String accountNumber;
}
