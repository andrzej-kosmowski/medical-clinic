package com.andrzej_kosmowski.patient;

import java.time.LocalDate;

public record UpdatePatientCommand(
        String email,
        String idCardNo,
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthday
) {
}
