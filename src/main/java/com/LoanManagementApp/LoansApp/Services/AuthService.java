package com.LoanManagementApp.LoansApp.Services;

import com.LoanManagementApp.LoansApp.Models.Role;
import com.LoanManagementApp.LoansApp.Models.User;
import com.LoanManagementApp.LoansApp.Repositories.RoleRepository;
import com.LoanManagementApp.LoansApp.Repositories.UserRepository;
import com.LoanManagementApp.LoansApp.security.JwtGenerator;
import com.LoanManagementApp.LoansApp.security.SecurityConstants;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @Transactional
    public User signup(String username, String password, String email, String roleName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        Role role = roleRepository.findByName("ROLE_" + roleName.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        user.setRoles(Collections.singleton(role));

        if ("LOAN_OFFICER".equalsIgnoreCase(roleName)) {
            user.setCanApproveLoans(true);
        }

        return userRepository.save(user);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        return jwtGenerator.generateToken(user);
    }

    @Transactional
    public User updateLoanApprovalPermission(Long userId, boolean canApproveLoans) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setCanApproveLoans(canApproveLoans);
        return userRepository.save(user); // Return the saved UserEntity
    }


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

        if (roleName != null) {
            String normalizedRole = "ROLE_" + roleName.toUpperCase();
            if (!normalizedRole.equals(SecurityConstants.ROLE_ADMIN) &&
                    !normalizedRole.equals(SecurityConstants.ROLE_LOAN_OFFICER)) {
                throw new IllegalArgumentException("Invalid role");
            }
            Role role = roleRepository.findByName(normalizedRole)
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.setRoles(Collections.singleton(role));
        }


        return userRepository.save(user);
    }


    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }


    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}