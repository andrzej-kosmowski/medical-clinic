package com.andrzej_kosmowski.medical_clinic.exception.doctor;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(Long id) {
        super("Doctor with id " + id + " not found", HttpStatus.NOT_FOUND);
    }
}
