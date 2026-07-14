package com.andrzej_kosmowski.medical_clinic.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidPatientDataException extends RuntimeException {
    public InvalidPatientDataException(String message) {
        super(message);
    }
}
