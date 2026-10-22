package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.MedicalClinicException;

public class VisitNotFoundException extends MedicalClinicException {
    public VisitNotFoundException(Long id) {
        super("Visit with id " + id + " not found", 404);
    }
}
