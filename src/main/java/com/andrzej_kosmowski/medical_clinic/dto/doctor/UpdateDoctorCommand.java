package com.andrzej_kosmowski.medical_clinic.dto.doctor;

public record UpdateDoctorCommand(
        String email,
        String firstName,
        String lastName,
        String specialization
) {
}
