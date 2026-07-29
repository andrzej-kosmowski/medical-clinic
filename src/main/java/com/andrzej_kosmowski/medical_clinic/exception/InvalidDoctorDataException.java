package com.andrzej_kosmowski.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class InvalidDoctorDataException extends MedicalClinicException {
    public InvalidDoctorDataException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
