package com.andrzej_kosmowski.medical_clinic.dto.doctor;

public record CreateDoctorCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String specialization,
        String facilityName
) {
}
