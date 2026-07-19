package com.andrzej_kosmowski.medical_clinic.repository;

import com.andrzej_kosmowski.medical_clinic.exception.PatientAlreadyExistsException;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepository {
    private final List<Patient> patients = new ArrayList<>();

    public List<Patient> findAll() {
        return new ArrayList<>(patients);
    }

    public Optional<Patient> findByEmail(String email) {
        return patients.stream()
                .filter(patient -> patient.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public Patient add(Patient patient) {
        boolean emailAlreadyExists = patients.stream()
                .anyMatch(existing -> existing.hasSameEmail(patient));
        if (emailAlreadyExists) {
            throw new PatientAlreadyExistsException(patient.getEmail());
        }
        patients.add(patient);
        return patient;
    }

    public Patient update(Patient patient) {
        boolean emailAlreadyTaken = patients.stream()
                .filter(existing -> existing != patient)
                .anyMatch(existing -> existing.hasSameEmail(patient));
        if (emailAlreadyTaken) {
            throw new PatientAlreadyExistsException(patient.getEmail());
        }
        return patient;
    }

    public void delete(Patient patient) {
        patients.remove(patient);
    }
}
