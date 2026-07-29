package com.andrzej_kosmowski.medical_clinic.exception.user;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends MedicalClinicException {
    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists", HttpStatus.CONFLICT);
    }
}
