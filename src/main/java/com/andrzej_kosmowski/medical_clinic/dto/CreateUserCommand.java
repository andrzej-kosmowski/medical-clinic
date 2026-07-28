package com.andrzej_kosmowski.medical_clinic.dto;

public record CreateUserCommand(
        String email,
        String password
) {
}
