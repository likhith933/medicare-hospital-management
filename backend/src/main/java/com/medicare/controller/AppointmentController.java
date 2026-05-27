package com.medicare.controller;

import com.medicare.dto.AppointmentRequest;
import com.medicare.entity.Appointment;
import com.medicare.exception.ApiResponse;
import com.medicare.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<ApiResponse<Appointment>> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentService.bookAppointment(request);
        ApiResponse<Appointment> response = ApiResponse.<Appointment>builder()
                .success(true)
                .message("Appointment booked successfully")
                .data(appointment)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<ApiResponse<Appointment>> cancelAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.cancelAppointment(id);
        ApiResponse<Appointment> response = ApiResponse.<Appointment>builder()
                .success(true)
                .message("Appointment cancelled successfully")
                .data(appointment)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reschedule/{id}")
    public ResponseEntity<ApiResponse<Appointment>> rescheduleAppointment(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        
        String dateStr = requestBody.get("appointmentDate");
        String slotStr = requestBody.get("timeSlot");

        if (dateStr == null || slotStr == null) {
            throw new IllegalArgumentException("appointmentDate and timeSlot are required");
        }

        LocalDate newDate = LocalDate.parse(dateStr);
        Appointment appointment = appointmentService.rescheduleAppointment(id, newDate, slotStr);
        
        ApiResponse<Appointment> response = ApiResponse.<Appointment>builder()
                .success(true)
                .message("Appointment rescheduled successfully")
                .data(appointment)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Appointment>>> getPatientAppointments(@PathVariable Long patientId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByPatient(patientId);
        ApiResponse<List<Appointment>> response = ApiResponse.<List<Appointment>>builder()
                .success(true)
                .message("Fetched appointments for patient")
                .data(appointments)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<Appointment>>> getDoctorAppointments(@PathVariable Long doctorId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        ApiResponse<List<Appointment>> response = ApiResponse.<List<Appointment>>builder()
                .success(true)
                .message("Fetched appointments for doctor")
                .data(appointments)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/confirm/{id}")
    public ResponseEntity<ApiResponse<Appointment>> confirmAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.confirmAppointment(id);
        ApiResponse<Appointment> response = ApiResponse.<Appointment>builder()
                .success(true)
                .message("Appointment confirmed successfully")
                .data(appointment)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/complete/{id}")
    public ResponseEntity<ApiResponse<Appointment>> completeAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.completeAppointment(id);
        ApiResponse<Appointment> response = ApiResponse.<Appointment>builder()
                .success(true)
                .message("Appointment marked as completed")
                .data(appointment)
                .build();
        return ResponseEntity.ok(response);
    }
}
