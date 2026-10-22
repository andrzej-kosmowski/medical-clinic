package com.andrzej_kosmowski.user;

import com.andrzej_kosmowski.MedicalClinicException;

public class UserAlreadyExistsException extends MedicalClinicException {
    public UserAlreadyExistsException(String email) {
        super("User with email " + email + " already exists", 409);
    }
}
