package com.andrzej_kosmowski.medical_clinic.dto.user;

public record UpdateUserCommand(
        String firstName,
        String lastName,
        String email
) {
}
