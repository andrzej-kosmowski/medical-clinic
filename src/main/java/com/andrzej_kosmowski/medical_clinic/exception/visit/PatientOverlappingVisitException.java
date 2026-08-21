package com.andrzej_kosmowski.medical_clinic.exception.visit;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class PatientOverlappingVisitException extends MedicalClinicException {
    public PatientOverlappingVisitException(Long id) {
        super("Patient with id " + id + " has another visit at this time", HttpStatus.CONFLICT);
    }
}
