package com.andrzej_kosmowski.medical_clinic.dto.doctor;

import com.andrzej_kosmowski.medical_clinic.dto.facility.FacilityShortDto;

import java.util.Set;

public record DoctorDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization,
        Set<FacilityShortDto> facilities
) {
}
