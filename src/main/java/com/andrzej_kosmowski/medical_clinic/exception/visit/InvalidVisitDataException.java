package com.andrzej_kosmowski.medical_clinic.exception.visit;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class InvalidVisitDataException extends MedicalClinicException {
    public InvalidVisitDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
