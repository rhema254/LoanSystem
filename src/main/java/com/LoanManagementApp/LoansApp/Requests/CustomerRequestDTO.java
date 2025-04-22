package com.LoanManagementApp.LoansApp.Requests;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CustomerRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String idNumber;
    private LocalDate dob;


}
