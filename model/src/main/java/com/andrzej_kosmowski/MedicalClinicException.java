package com.andrzej_kosmowski;
import lombok.Getter;

@Getter
public abstract class MedicalClinicException extends RuntimeException {
    private final Integer status;

    protected MedicalClinicException(String message, int status) {
        super(message);
        this.status = status;
    }
}
