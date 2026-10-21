package com.andrzej_kosmowski.doctor;

import java.util.Set;

public record CreateDoctorCommand(
        String firstName,
        String lastName,
        String email,
        String password,
        String specialization,
        Set<Long> facilityIds
) {
}
