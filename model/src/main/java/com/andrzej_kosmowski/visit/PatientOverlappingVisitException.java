package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.MedicalClinicException;

public class PatientOverlappingVisitException extends MedicalClinicException {
    public PatientOverlappingVisitException(Long id) {
        super("Patient with id " + id + " has another visit at this time", 409);
    }
}
