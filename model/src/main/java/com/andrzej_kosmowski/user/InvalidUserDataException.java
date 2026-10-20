package com.andrzej_kosmowski.user;

import com.andrzej_kosmowski.MedicalClinicException;

public class InvalidUserDataException extends MedicalClinicException {
    public InvalidUserDataException(String message) {
        super(message, 400);
    }
}
