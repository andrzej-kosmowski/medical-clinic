package com.andrzej_kosmowski.medical_clinic.dto;

import java.time.LocalDateTime;

public record ErrorMessageDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {
}
