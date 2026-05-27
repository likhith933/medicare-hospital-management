package com.medicare.controller;

import com.medicare.entity.Doctor;
import com.medicare.exception.ApiResponse;
import com.medicare.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<Doctor>>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        ApiResponse<List<Doctor>> response = ApiResponse.<List<Doctor>>builder()
                .success(true)
                .message("Fetched all doctors")
                .data(doctors)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{specialization}")
    public ResponseEntity<ApiResponse<List<Doctor>>> getDoctorsBySpecialization(@PathVariable String specialization) {
        List<Doctor> doctors = doctorService.getDoctorsBySpecialization(specialization);
        ApiResponse<List<Doctor>> response = ApiResponse.<List<Doctor>>builder()
                .success(true)
                .message("Fetched doctors specialized in " + specialization)
                .data(doctors)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/slots")
    public ResponseEntity<ApiResponse<String>> getDoctorAvailableSlots(@PathVariable Long id) {
        String slots = doctorService.getDoctorAvailableSlots(id);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Fetched available slots for doctor")
                .data(slots)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Doctor>> registerDoctor(@RequestBody Doctor doctor) {
        Doctor savedDoctor = doctorService.registerDoctor(doctor);
        ApiResponse<Doctor> response = ApiResponse.<Doctor>builder()
                .success(true)
                .message("Doctor registered successfully")
                .data(savedDoctor)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
