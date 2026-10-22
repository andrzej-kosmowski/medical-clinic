package com.andrzej_kosmowski;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class MedicalClinicExceptionHandler {
    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ErrorMessageDto> handleMedicalClinicException(
            MedicalClinicException exception
    ) {
        log.error("Business exception: status={}, message={}", exception.getStatus(), exception.getMessage());
        Integer status = exception.getStatus();
        ErrorMessageDto error = new ErrorMessageDto(
                LocalDateTime.now(),
                status,
                "Error",
                exception.getMessage()
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessageDto> handleUnexpectedException(
            Exception exception
    ) {
        log.error("Unexpected exception occurred: {}", exception.getMessage(), exception);
        ErrorMessageDto error = new ErrorMessageDto(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                exception.getMessage()
        );
        return ResponseEntity
                .internalServerError()
                .body(error);
    }
}
