package com.andrzej_kosmowski.facility;

import com.andrzej_kosmowski.MedicalClinicException;

public class FacilityAlreadyExistsException extends MedicalClinicException {
    public FacilityAlreadyExistsException(String name) {
        super("Facility with name " + name + " already exists", 409);
    }
}
