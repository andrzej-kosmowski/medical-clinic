package com.andrzej_kosmowski.doctor;

import com.andrzej_kosmowski.MedicalClinicException;

public class DoctorNotFoundException extends MedicalClinicException {
    public DoctorNotFoundException(Long id) {
        super("Doctor with id " + id + " not found", 404);
    }
}
