package com.andrzej_kosmowski.medical_clinic.dto;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
