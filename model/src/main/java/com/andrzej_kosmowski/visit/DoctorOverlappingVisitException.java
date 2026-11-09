package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.MedicalClinicException;

public class DoctorOverlappingVisitException extends MedicalClinicException {
    public DoctorOverlappingVisitException(Long id) {
        super("Doctor with id " + id + " has overlapping visit dates.", 409);
    }
}
