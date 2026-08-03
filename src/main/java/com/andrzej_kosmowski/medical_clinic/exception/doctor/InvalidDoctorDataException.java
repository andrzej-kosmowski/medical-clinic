package com.andrzej_kosmowski.medical_clinic.exception.doctor;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class InvalidDoctorDataException extends MedicalClinicException {
    public InvalidDoctorDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
