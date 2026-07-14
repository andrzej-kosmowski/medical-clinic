package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.exception.PatientNotFoundException;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import com.andrzej_kosmowski.medical_clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }

    public Patient addPatient(Patient patient) {
        patient.validate();
        return patientRepository.save(patient);
    }

    public void deletePatientByEmail(String email) {
        Patient patient = getPatientByEmail(email);
        patientRepository.delete(patient);
    }

    public Patient updatePatientByEmail(String email, Patient updatedPatient) {
        Patient existing = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        existing.update(updatedPatient);
        return patientRepository.save(existing);
    }

    public Patient changePassword(String email, String newPassword) {
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
        patient.setPassword(newPassword);
        return patientRepository.save(patient);
    }
}
