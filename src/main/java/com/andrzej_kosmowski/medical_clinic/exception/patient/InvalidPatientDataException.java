package com.andrzej_kosmowski.medical_clinic.exception.patient;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class InvalidPatientDataException extends MedicalClinicException {
    public InvalidPatientDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
