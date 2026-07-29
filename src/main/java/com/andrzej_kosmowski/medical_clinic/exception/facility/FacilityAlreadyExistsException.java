package com.andrzej_kosmowski.medical_clinic.exception.facility;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class FacilityAlreadyExistsException extends MedicalClinicException {
    public FacilityAlreadyExistsException(String name) {
        super("Facility with name " + name + " already exists", HttpStatus.CONFLICT);
    }
}
