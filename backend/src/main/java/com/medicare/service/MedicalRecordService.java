package com.medicare.service;

import com.medicare.entity.Doctor;
import com.medicare.entity.MedicalRecord;
import com.medicare.entity.Patient;
import com.medicare.exception.ResourceNotFoundException;
import com.medicare.repository.DoctorRepository;
import com.medicare.repository.MedicalRecordRepository;
import com.medicare.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    public MedicalRecord addMedicalRecord(Long patientId, Long doctorId, String diagnosis, String prescription, String labReports, LocalDate visitDate) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", patientId));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis(diagnosis)
                .prescription(prescription)
                .labReports(labReports)
                .visitDate(visitDate != null ? visitDate : LocalDate.now())
                .build();

        return medicalRecordRepository.save(record);
    }

    public List<MedicalRecord> getMedicalRecordsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient", "id", patientId);
        }
        return medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId);
    }

    public MedicalRecord updateMedicalRecord(Long id, String diagnosis, String prescription, String labReports) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord", "id", id));
        
        record.setDiagnosis(diagnosis);
        record.setPrescription(prescription);
        record.setLabReports(labReports);
        
        return medicalRecordRepository.save(record);
    }
}
