package com.LoanManagementApp.LoansApp.Responses;

import lombok.Data;

import java.util.Set;

@Data
public class CreateUserResponse {
    private Long id;
    private String username;
    private String email;
    private String role;

}
