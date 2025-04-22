package com.LoanManagementApp.LoansApp.Requests;

import lombok.Data;

import com.LoanManagementApp.LoansApp.Enums.Role;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String email;
    private Role role;
}
