package com.andrzej_kosmowski.medical_clinic.dto;

import java.time.LocalDate;

public record PatientDto(
        String email,
        String idCarNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}
