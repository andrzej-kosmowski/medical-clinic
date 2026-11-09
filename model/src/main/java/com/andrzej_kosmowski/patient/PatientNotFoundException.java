package com.andrzej_kosmowski.patient;

import com.andrzej_kosmowski.MedicalClinicException;

public class PatientNotFoundException extends MedicalClinicException {
    public PatientNotFoundException(Long id) {
        super("Patient with id " + id + " not found", 404);
    }
}
