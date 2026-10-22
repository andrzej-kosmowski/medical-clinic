package com.andrzej_kosmowski.patient;

import com.andrzej_kosmowski.MedicalClinicException;

public class InvalidPatientDataException extends MedicalClinicException {
    public InvalidPatientDataException(String message) {
        super(message, 400);
    }
}
