package com.andrzej_kosmowski.patient;

import com.andrzej_kosmowski.MedicalClinicException;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(String idCardNo) {
        super("Patient with id card number " + idCardNo + " already exists", 409);
    }
}
