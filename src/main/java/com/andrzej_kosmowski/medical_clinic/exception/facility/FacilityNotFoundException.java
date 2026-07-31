package com.andrzej_kosmowski.medical_clinic.exception.facility;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

import java.util.Set;
import java.util.stream.Collectors;

public class FacilityNotFoundException extends MedicalClinicException {
    public FacilityNotFoundException(Long id) {
        super("Facility with name " + id + " not found", HttpStatus.NOT_FOUND);
    }

    public FacilityNotFoundException(Set<Long> ids) {
        super(createMessage(ids), HttpStatus.NOT_FOUND);
    }

    private static String createMessage(Set<Long> ids) {
        String facilities = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        if (ids.size() == 1) {
            return String.format("Facility with name %s not found", facilities);
        }
        return String.format("Facilities %s not found", facilities);
    }
}
