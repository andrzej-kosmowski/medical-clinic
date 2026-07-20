package com.andrzej_kosmowski.medical_clinic.dto;

import java.time.LocalDate;

public record CreatePatientCommand(
        String email,
        String password,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}
