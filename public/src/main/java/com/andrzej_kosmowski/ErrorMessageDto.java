package com.andrzej_kosmowski;

import java.time.LocalDateTime;

public record ErrorMessageDto(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {
}
