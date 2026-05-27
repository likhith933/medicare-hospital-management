package com.medicare.controller;

import com.medicare.entity.Appointment;
import com.medicare.exception.ApiResponse;
import com.medicare.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getStats() {
        Map<String, Long> stats = adminService.getStats();
        ApiResponse<Map<String, Long>> response = ApiResponse.<Map<String, Long>>builder()
                .success(true)
                .message("Fetched administrative statistics")
                .data(stats)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/appointments/all")
    public ResponseEntity<ApiResponse<List<Appointment>>> getAllAppointments() {
        List<Appointment> appointments = adminService.getAllAppointments();
        ApiResponse<List<Appointment>> response = ApiResponse.<List<Appointment>>builder()
                .success(true)
                .message("Fetched all hospital appointments")
                .data(appointments)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/patient/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long id) {
        adminService.deletePatient(id);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(true)
                .message("Patient account and associated records deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}
