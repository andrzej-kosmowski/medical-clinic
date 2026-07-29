package com.andrzej_kosmowski.medical_clinic.dto;

public record UpdateUserCommand(
        String firstName,
        String lastName,
        String email
) {
}
