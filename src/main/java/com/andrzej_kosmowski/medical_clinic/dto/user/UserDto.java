package com.andrzej_kosmowski.medical_clinic.dto.user;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
