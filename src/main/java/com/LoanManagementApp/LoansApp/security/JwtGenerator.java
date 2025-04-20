package com.LoanManagementApp.LoansApp.security;

import com.LoanManagementApp.LoansApp.Models.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtGenerator {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(User user) {
        List<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toList());

        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA512");

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roles)
                .claim("canApproveLoans", user.isCanApproveLoans())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }
}