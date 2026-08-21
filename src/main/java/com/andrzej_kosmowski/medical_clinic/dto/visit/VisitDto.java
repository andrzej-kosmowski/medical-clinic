package com.andrzej_kosmowski.medical_clinic.dto.visit;

import java.time.LocalDateTime;

public record VisitDto(
        Long id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long doctorId,
        Long patientId
) {
}
