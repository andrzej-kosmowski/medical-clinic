package com.andrzej_kosmowski.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class InvalidFacilityDataException extends MedicalClinicException {
    public InvalidFacilityDataException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
