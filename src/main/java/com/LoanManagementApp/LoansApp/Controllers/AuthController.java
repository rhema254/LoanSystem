package com.LoanManagementApp.LoansApp.Controllers;

import com.LoanManagementApp.LoansApp.Models.User;
import com.LoanManagementApp.LoansApp.Requests.CreateUserRequest;
import com.LoanManagementApp.LoansApp.Responses.CreateUserResponse;
import com.LoanManagementApp.LoansApp.Services.AuthService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
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
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody CreateUserRequest createUserRequest) {
        CreateUserResponse createdUser = authService.signup(createUserRequest);
        return new ResponseEntity<>(
                Map.of(
                        "message", "User registered successfully",
                        "user", createdUser
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

        User updatedUser = authService.updateUser(id, username, email, password, role);
        return ResponseEntity.ok(
                Map.of(
                        "message", "User updated successfully",
                        "user", updatedUser
                )
        );
    }


    @GetMapping("/users")
    public List<User> getusers(){
        return authService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        String deletedUser =  authService.deleteUser(id);
        return new ResponseEntity<>( deletedUser, HttpStatus.NO_CONTENT);
    }

}
