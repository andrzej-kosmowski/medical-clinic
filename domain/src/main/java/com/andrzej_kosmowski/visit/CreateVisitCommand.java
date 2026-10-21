package com.andrzej_kosmowski.visit;

import java.time.LocalDateTime;

public record CreateVisitCommand(
        Long doctorId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
