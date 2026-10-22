package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.MedicalClinicException;

public class VisitAlreadyBookedException extends MedicalClinicException {
    public VisitAlreadyBookedException(Long id) {
        super("Visit with id " + id + " already booked", 409);
    }
}
