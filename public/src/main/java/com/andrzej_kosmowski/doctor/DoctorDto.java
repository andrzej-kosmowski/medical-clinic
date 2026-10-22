package com.andrzej_kosmowski.doctor;

import java.util.Set;

public record DoctorDto(
        Long id,
        String specialization,
        Set<Long> facilityIds
) {
}
