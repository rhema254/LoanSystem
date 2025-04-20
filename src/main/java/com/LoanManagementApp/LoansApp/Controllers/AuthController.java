package com.LoanManagementApp.LoansApp.Controllers;

import com.LoanManagementApp.LoansApp.Models.User;
import com.LoanManagementApp.LoansApp.Services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        String email = request.get("email");
        String role = request.get("role");

        User user = authService.signup(username, password, email, role);
        return new ResponseEntity<>(
                Map.of(
                        "message", "User registered successfully",
                        "user", user
                ),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        String token = authService.login(username, password);
        return ResponseEntity.ok(Map.of("token", token, "tokenType", "Bearer" ));
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    @PutMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");
        String password = request.get("password");
        String role = request.get("role");
        Boolean canApproveLoans = request.containsKey("canApproveLoans") ?
                Boolean.parseBoolean(request.get("canApproveLoans")) : null;

        User updatedUser = authService.updateUser(id, username, email, password, role);
        return ResponseEntity.ok(
                Map.of(
                        "message", "User updated successfully",
                        "user", updatedUser
                )
        );
    }


//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/permissions")
    public ResponseEntity<User> updateLoanApprovalPermission(
            @PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        boolean canApproveLoans = request.getOrDefault("canApproveLoans", false);
        User updatedUser = authService.updateLoanApprovalPermission(id, canApproveLoans);

        return ResponseEntity.ok(updatedUser);
    }


    @GetMapping("/users")
    public List<User> getusers(){
        return authService.getAllUsers();
    }

}
