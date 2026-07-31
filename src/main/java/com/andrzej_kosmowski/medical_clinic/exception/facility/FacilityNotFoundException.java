package com.andrzej_kosmowski.medical_clinic.exception.facility;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

import java.util.Set;

public class FacilityNotFoundException extends MedicalClinicException {
    public FacilityNotFoundException(String name) {
        super("Facility with name " + name + " not found", HttpStatus.NOT_FOUND);
    }

    public FacilityNotFoundException(Set<String> names) {
        super(createMessage(names), HttpStatus.NOT_FOUND);
    }

    private static String createMessage(Set<String> names) {
        String facilities = String.join(", ", names);
        if (names.size() == 1) {
            return String.format("Facility with name %s not found", facilities);
        }
        return String.format("Facilities %s not found", facilities);
    }
}
