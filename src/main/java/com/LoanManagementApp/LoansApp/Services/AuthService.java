package com.LoanManagementApp.LoansApp.Services;

import com.LoanManagementApp.LoansApp.Enums.Role;
import com.LoanManagementApp.LoansApp.Models.User;
import com.LoanManagementApp.LoansApp.Repositories.UserRepository;
import com.LoanManagementApp.LoansApp.Requests.CreateUserRequest;
import com.LoanManagementApp.LoansApp.Responses.CreateUserResponse;
import com.LoanManagementApp.LoansApp.security.JwtGenerator;
import com.LoanManagementApp.LoansApp.security.SecurityConstants;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtGenerator jwtGenerator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }
//---- SignUp Method----
    @Transactional
    public CreateUserResponse signup(CreateUserRequest createUserRequest) {
        if (userRepository.existsByUsername(createUserRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(createUserRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(passwordEncoder.encode(createUserRequest.getPassword()));
        user.setEmail(createUserRequest.getEmail());
        user.setRole(createUserRequest.getRole());


        User newUser = userRepository.save(user);
        return convertToResponseDTO(newUser);
    }

//--- Login Method ---
    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return jwtGenerator.generateToken(user);
    }

//---- Update Method ----
    @Transactional
    public User updateUser(Long id, String username, String email, String password, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (username != null && !username.equals(user.getUsername())) {
            if (userRepository.existsByUsername(username)) {
                throw new IllegalArgumentException("Username already exists");
            }
            user.setUsername(username);
        }

        if (email != null && !email.equals(user.getEmail())) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(email);
        }

        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        if (roleName != null && !password.isBlank()){
            user.setRole(Role.valueOf(roleName));
        }

        return userRepository.save(user);
    }

//---- Delete User ----
    @Transactional
    public String deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
        String message = "User Deleted Successfully";
        return message;
    }


    public List<User> getAllUsers(){
        return userRepository.findAll();
    }


    public CreateUserResponse convertToResponseDTO(User user){
        CreateUserResponse responseDTO = new CreateUserResponse();
        responseDTO.setId(user.getId());
        responseDTO.setUsername(user.getUsername());
        responseDTO.setEmail(user.getEmail());
        responseDTO.setRole(user.getRole().name());

        return responseDTO;
    }
}