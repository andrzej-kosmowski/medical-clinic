package com.andrzej_kosmowski.user;

import com.andrzej_kosmowski.MedicalClinicException;

public class UserNotFoundException extends MedicalClinicException {
    public UserNotFoundException(Long id) {
        super("User with id " + id + " not found", 404);
    }
}