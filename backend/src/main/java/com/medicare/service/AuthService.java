package com.medicare.service;

import com.medicare.config.JwtConfig;
import com.medicare.dto.*;
import com.medicare.entity.*;
import com.medicare.repository.*;
import com.medicare.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class AuthService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtConfig jwtConfig;

    public Patient registerPatient(RegisterRequest request) {
        if (patientRepository.existsByEmail(request.getEmail()) || 
            doctorRepository.existsByEmail(request.getEmail()) || 
            adminRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        Patient patient = Patient.builder()
                .name(request.getName())
                .age(request.getAge())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .bloodGroup(request.getBloodGroup())
                .build();

        return patientRepository.save(patient);
    }

    public JwtResponse login(LoginRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();
        String role = request.getRole();

        Long id = null;
        String name = null;
        String mappedRole = null;
        String dbPassword = null;

        if ("PATIENT".equalsIgnoreCase(role)) {
            Patient patient = patientRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
            id = patient.getId();
            name = patient.getName();
            dbPassword = patient.getPassword();
            mappedRole = "ROLE_PATIENT";
        } else if ("DOCTOR".equalsIgnoreCase(role)) {
            Doctor doctor = doctorRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
            id = doctor.getId();
            name = doctor.getName();
            dbPassword = doctor.getPassword();
            mappedRole = "ROLE_DOCTOR";
        } else if ("ADMIN".equalsIgnoreCase(role)) {
            Admin admin = adminRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));
            id = admin.getId();
            name = admin.getName();
            dbPassword = admin.getPassword();
            mappedRole = "ROLE_ADMIN";
        } else {
            throw new IllegalArgumentException("Invalid role requested");
        }

        if (!passwordEncoder.matches(password, dbPassword)) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Generate Access & Refresh tokens
        String accessToken = jwtTokenProvider.generateTokenFromUsername(email, mappedRole);
        
        // Remove old refresh tokens for this email
        refreshTokenRepository.deleteByUserEmail(email);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .userEmail(email)
                .userRole(mappedRole)
                .expiryDate(Instant.now().plusMillis(jwtConfig.getRefreshExpirationMs()))
                .build();
        refreshTokenRepository.save(refreshToken);

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .id(id)
                .name(name)
                .email(email)
                .role(mappedRole)
                .build();
    }

    public JwtResponse refreshAccessToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token was expired. Please sign in again.");
        }

        String email = refreshToken.getUserEmail();
        String role = refreshToken.getUserRole();
        Long id = null;
        String name = null;

        if ("ROLE_PATIENT".equals(role)) {
            Patient p = patientRepository.findByEmail(email).orElseThrow();
            id = p.getId();
            name = p.getName();
        } else if ("ROLE_DOCTOR".equals(role)) {
            Doctor d = doctorRepository.findByEmail(email).orElseThrow();
            id = d.getId();
            name = d.getName();
        } else if ("ROLE_ADMIN".equals(role)) {
            Admin a = adminRepository.findByEmail(email).orElseThrow();
            id = a.getId();
            name = a.getName();
        }

        String newAccessToken = jwtTokenProvider.generateTokenFromUsername(email, role);

        return JwtResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .id(id)
                .name(name)
                .email(email)
                .role(role)
                .build();
    }

    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(refreshTokenRepository::delete);
    }
}
