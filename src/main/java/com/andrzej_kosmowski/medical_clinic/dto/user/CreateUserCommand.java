package com.andrzej_kosmowski.medical_clinic.dto.user;

public record CreateUserCommand(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
