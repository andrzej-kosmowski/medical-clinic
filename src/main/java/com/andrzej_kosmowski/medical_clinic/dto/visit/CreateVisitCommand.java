package com.andrzej_kosmowski.medical_clinic.dto.visit;

import java.time.LocalDateTime;

public record CreateVisitCommand(
        Long doctorId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
