package com.medicare.controller;

import com.medicare.entity.MedicalRecord;
import com.medicare.exception.ApiResponse;
import com.medicare.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<MedicalRecord>> addMedicalRecord(@RequestBody Map<String, Object> requestBody) {
        if (requestBody.get("patientId") == null || requestBody.get("doctorId") == null) {
            throw new IllegalArgumentException("patientId and doctorId are required");
        }
        Long patientId = Long.valueOf(requestBody.get("patientId").toString());
        Long doctorId = Long.valueOf(requestBody.get("doctorId").toString());
        String diagnosis = requestBody.get("diagnosis") != null ? requestBody.get("diagnosis").toString() : "";
        String prescription = requestBody.get("prescription") != null ? requestBody.get("prescription").toString() : "";
        
        Object labReportsObj = requestBody.get("labReports");
        String labReports = labReportsObj != null ? labReportsObj.toString() : null;

        Object visitDateObj = requestBody.get("visitDate");
        LocalDate visitDate = visitDateObj != null ? LocalDate.parse(visitDateObj.toString()) : LocalDate.now();

        MedicalRecord record = medicalRecordService.addMedicalRecord(patientId, doctorId, diagnosis, prescription, labReports, visitDate);
        ApiResponse<MedicalRecord> response = ApiResponse.<MedicalRecord>builder()
                .success(true)
                .message("Medical record added successfully")
                .data(record)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{patientId}")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getPatientMedicalRecords(@PathVariable Long patientId) {
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByPatient(patientId);
        ApiResponse<List<MedicalRecord>> response = ApiResponse.<List<MedicalRecord>>builder()
                .success(true)
                .message("Fetched medical records for patient")
                .data(records)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicalRecord>> updateMedicalRecord(
            @PathVariable Long id,
            @RequestBody Map<String, String> requestBody) {
        
        String diagnosis = requestBody.get("diagnosis");
        String prescription = requestBody.get("prescription");
        String labReports = requestBody.get("labReports");

        MedicalRecord record = medicalRecordService.updateMedicalRecord(id, diagnosis, prescription, labReports);
        
        ApiResponse<MedicalRecord> response = ApiResponse.<MedicalRecord>builder()
                .success(true)
                .message("Medical record updated successfully")
                .data(record)
                .build();
        return ResponseEntity.ok(response);
    }
}
