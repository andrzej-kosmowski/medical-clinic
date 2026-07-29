package com.andrzej_kosmowski.medical_clinic.exception.user;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends MedicalClinicException {
    public UserNotFoundException(String email) {
        super("User with email " + email + " not found", HttpStatus.NOT_FOUND);
    }
}