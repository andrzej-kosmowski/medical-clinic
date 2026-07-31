package com.andrzej_kosmowski.medical_clinic.exception.patient;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException(Long id) {
        super("Patient with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
