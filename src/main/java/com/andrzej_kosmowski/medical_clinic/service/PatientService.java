package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.exception.PatientAlreadyExistsException;
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
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new PatientAlreadyExistsException(patient.getEmail());
        }
        return patientRepository.save(patient);
    }

    public void deletePatientByEmail(String email) {
        Patient patient = getPatientByEmail(email);

        patientRepository.delete(patient);
    }

    public Patient updatePatientByEmail(String email, Patient updatedPatient) {
        Patient existing = getPatientByEmail(email);

        existing.setFirstName(updatedPatient.getFirstName());
        existing.setLastName(updatedPatient.getLastName());
        existing.setPhoneNumber(updatedPatient.getPhoneNumber());
        existing.setBirthday(updatedPatient.getBirthday());

        return patientRepository.save(existing);
    }
}
