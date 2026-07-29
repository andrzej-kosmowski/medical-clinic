package com.andrzej_kosmowski.medical_clinic.dto;

public record UpdateDoctorCommand(
        String email,
        String firstName,
        String lastName,
        String specialization
) {
}
