package com.andrzej_kosmowski.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(String message) {
        super("Patient with email " + message + " already exists", HttpStatus.CONFLICT);
    }
}
