package com.andrzej_kosmowski.medical_clinic.dto;

public record CreateDoctorCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String specialization,
        String facilityName
) {
}
