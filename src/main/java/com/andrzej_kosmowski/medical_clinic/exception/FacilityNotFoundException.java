package com.andrzej_kosmowski.medical_clinic.exception;

import org.springframework.http.HttpStatus;

public class FacilityNotFoundException extends MedicalClinicException {
    public FacilityNotFoundException(String name) {
        super("Facility with name " + name + " not found", HttpStatus.NOT_FOUND);
    }
}
