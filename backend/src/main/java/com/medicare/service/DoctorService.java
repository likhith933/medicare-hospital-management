package com.medicare.service;

import com.medicare.entity.Doctor;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationIgnoreCase(specialization);
    }

    public String getDoctorAvailableSlots(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", id));
        return doctor.getAvailableSlots();
    }

    public Doctor registerDoctor(Doctor doctor) {
        if (doctor.getEmail() == null || doctor.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required for registration");
        }
        if (doctorRepository.existsByEmail(doctor.getEmail())) {
            throw new IllegalArgumentException("Doctor email is already registered");
        }
        // BCrypt hash doctor password
        if (doctor.getPassword() == null) {
            doctor.setPassword("Doctor123!"); // default fallback
        }
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        if (doctor.getRating() == null) {
            doctor.setRating(5.0);
        }
        return doctorRepository.save(doctor);
    }
}
