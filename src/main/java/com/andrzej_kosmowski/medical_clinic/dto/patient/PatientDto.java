package com.andrzej_kosmowski.medical_clinic.dto.patient;

import java.time.LocalDate;

public record PatientDto(
        String email,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}
