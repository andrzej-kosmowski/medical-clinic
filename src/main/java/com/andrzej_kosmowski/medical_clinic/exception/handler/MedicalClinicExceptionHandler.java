package com.andrzej_kosmowski.medical_clinic.exception.handler;

import com.andrzej_kosmowski.medical_clinic.dto.ErrorMessageDto;
import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class MedicalClinicExceptionHandler {
    @ExceptionHandler(MedicalClinicException.class)
    public ResponseEntity<ErrorMessageDto> handleMedicalClinicException(MedicalClinicException exception) {
        HttpStatus status = exception.getStatus();
        ErrorMessageDto error = new ErrorMessageDto(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage()
        );

        return ResponseEntity
                .status(status)
                .body(error);
    }
}
