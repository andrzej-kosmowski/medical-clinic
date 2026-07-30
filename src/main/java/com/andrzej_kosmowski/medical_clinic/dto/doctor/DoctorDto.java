package com.andrzej_kosmowski.medical_clinic.dto.doctor;

import java.util.Set;

public record DoctorDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization,
        Set<String> facilityNames
) {
}
