package com.andrzej_kosmowski.doctor;

import com.andrzej_kosmowski.MedicalClinicException;

public class InvalidDoctorDataException extends MedicalClinicException {
    public InvalidDoctorDataException(String message) {
        super(message, 400);
    }
}
