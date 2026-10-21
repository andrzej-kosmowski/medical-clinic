package com.andrzej_kosmowski.doctor;

public record UpdateDoctorCommand(
        String firstName,
        String lastName,
        String email,
        String specialization
) {
}
