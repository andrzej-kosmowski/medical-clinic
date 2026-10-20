package com.andrzej_kosmowski.medical_clinic.dto.visit;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record VisitSearchCriteria(
        String specialization,
        LocalDate date,
        LocalDateTime from,
        LocalDateTime to,
        boolean availableOnly
) {
    public LocalDateTime effectiveFrom() {
        if (date != null && from == null && to == null) {
            return date.atStartOfDay();
        }
        return from;
    }

    public LocalDateTime effectiveTo() {
        if (date != null && to == null && from == null) {
            return date.plusDays(1).atStartOfDay();
        }
        return to;
    }
}
