package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.MedicalClinicException;

import java.time.LocalDateTime;

public class PastVisitException extends MedicalClinicException {
    public PastVisitException(LocalDateTime startTime) {
        super("Visit cannot be bookend in the past: " + startTime, 400);
    }
}
