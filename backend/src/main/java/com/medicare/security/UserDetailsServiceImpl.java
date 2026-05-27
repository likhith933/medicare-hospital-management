package com.medicare.security;

import com.medicare.entity.Admin;
import com.medicare.entity.Doctor;
import com.medicare.entity.Patient;
import com.medicare.repository.AdminRepository;
import com.medicare.repository.DoctorRepository;
import com.medicare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try Admin
        Optional<Admin> admin = adminRepository.findByEmail(email);
        if (admin.isPresent()) {
            String role = admin.get().getRole();
            if (!role.startsWith("ROLE_")) {
                role = "ROLE_" + role;
            }
            return new User(
                    admin.get().getEmail(),
                    admin.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );
        }

        // Try Doctor
        Optional<Doctor> doctor = doctorRepository.findByEmail(email);
        if (doctor.isPresent()) {
            return new User(
                    doctor.get().getEmail(),
                    doctor.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_DOCTOR"))
            );
        }

        // Try Patient
        Optional<Patient> patient = patientRepository.findByEmail(email);
        if (patient.isPresent()) {
            return new User(
                    patient.get().getEmail(),
                    patient.get().getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_PATIENT"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
