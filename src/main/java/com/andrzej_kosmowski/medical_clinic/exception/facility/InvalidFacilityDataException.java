package com.andrzej_kosmowski.medical_clinic.exception.facility;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class InvalidFacilityDataException extends MedicalClinicException {
    public InvalidFacilityDataException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
