package com.andrzej_kosmowski.facility;

import com.andrzej_kosmowski.MedicalClinicException;

public class InvalidFacilityDataException extends MedicalClinicException {
    public InvalidFacilityDataException(String message) {
        super(message, 400);
    }
}
