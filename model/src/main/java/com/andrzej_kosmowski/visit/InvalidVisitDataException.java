package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.MedicalClinicException;

public class InvalidVisitDataException extends MedicalClinicException {
    public InvalidVisitDataException(String message) {
        super(message, 400);
    }
}
