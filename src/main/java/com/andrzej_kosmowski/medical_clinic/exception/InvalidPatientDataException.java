package com.andrzej_kosmowski.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class InvalidPatientDataException extends MedicalClinicException {
    public InvalidPatientDataException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
