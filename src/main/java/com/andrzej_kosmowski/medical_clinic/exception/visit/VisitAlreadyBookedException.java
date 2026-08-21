package com.andrzej_kosmowski.medical_clinic.exception.visit;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class VisitAlreadyBookedException extends MedicalClinicException {
    public VisitAlreadyBookedException(Long id) {
        super("Visit with id " + id + " already booked", HttpStatus.CONFLICT);
    }
}
