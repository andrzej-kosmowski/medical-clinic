package com.andrzej_kosmowski.medical_clinic.exception.doctor;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(String email) {
        super("Doctor with email " + email + " not found", HttpStatus.NOT_FOUND);
    }
}
