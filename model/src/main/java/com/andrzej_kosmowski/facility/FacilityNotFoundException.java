package com.andrzej_kosmowski.facility;

import com.andrzej_kosmowski.MedicalClinicException;

import java.util.Set;
import java.util.stream.Collectors;

public class FacilityNotFoundException extends MedicalClinicException {
    public FacilityNotFoundException(Long id) {
        super("Facility with id " + id + " not found", 404);
    }

    public FacilityNotFoundException(Set<Long> ids) {
        super(createMessage(ids), 404);
    }

    private static String createMessage(Set<Long> ids) {
        String facilities = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        if (ids.size() == 1) {
            return String.format("Facility with id %s not found", facilities);
        }
        return String.format("Facilities %s not found", facilities);
    }
}
