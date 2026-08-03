package com.andrzej_kosmowski.medical_clinic.exception.visit;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class PastVisitException extends MedicalClinicException {
    public PastVisitException(LocalDateTime startTime) {
        super("Visit cannot be bookend in the past: " + startTime, HttpStatus.BAD_REQUEST);
    }
}
