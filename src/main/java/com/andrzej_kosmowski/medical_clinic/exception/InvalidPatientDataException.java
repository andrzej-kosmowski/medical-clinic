package com.andrzej_kosmowski.medical_clinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class InvalidPatientDataException extends MedicalClinicException {
    public InvalidPatientDataException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
