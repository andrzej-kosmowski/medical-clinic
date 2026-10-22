package com.andrzej_kosmowski.patient;

import java.time.LocalDate;

public record PatientDto(
        Long id,
        String idCardNo,
        String phoneNumber,
        LocalDate birthday,
        Long userId
) {
}
