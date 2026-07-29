package com.andrzej_kosmowski.medical_clinic.exception.user;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class InvalidUserDataException extends MedicalClinicException {
    public InvalidUserDataException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
