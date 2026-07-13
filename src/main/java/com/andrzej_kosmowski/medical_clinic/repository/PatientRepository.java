package com.andrzej_kosmowski.medical_clinic.repository;

import com.andrzej_kosmowski.medical_clinic.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {
    private final List< Patient> patients = new ArrayList<>();

    public List<Patient> findAll() {
        return patients;
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equals(email))
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        return patients.stream()
                .anyMatch(patient -> patient.getEmail().equals(email));
    }

    public Patient save(Patient patient) {
        if (!patients.contains(patient)) {
            patients.add(patient);
        }

        return patient;
    }

    public void delete(Patient patient) {
        patients.remove(patient);
    }
}
