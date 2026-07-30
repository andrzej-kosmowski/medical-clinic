package com.andrzej_kosmowski.medical_clinic.dto.doctor;

import java.util.Set;

public record CreateDoctorCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String specialization,
        Set<String> facilityNames
) {
}
