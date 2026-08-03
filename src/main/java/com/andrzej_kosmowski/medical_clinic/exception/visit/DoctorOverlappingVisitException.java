package com.andrzej_kosmowski.medical_clinic.exception.visit;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class DoctorOverlappingVisitException extends MedicalClinicException {
    public DoctorOverlappingVisitException(Long id) {
        super("Doctor with id " + id + " has overlapping visit dates.", HttpStatus.CONFLICT);
    }
}
