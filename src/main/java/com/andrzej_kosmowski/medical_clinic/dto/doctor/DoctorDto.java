package com.andrzej_kosmowski.medical_clinic.dto.doctor;

public record DoctorDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        String specialization,
        String facilityName
) {
}
