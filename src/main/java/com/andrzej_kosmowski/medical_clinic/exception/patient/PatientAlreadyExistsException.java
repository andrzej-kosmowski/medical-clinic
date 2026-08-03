package com.andrzej_kosmowski.medical_clinic.exception.patient;

import com.andrzej_kosmowski.medical_clinic.exception.MedicalClinicException;
import org.springframework.http.HttpStatus;

public class PatientAlreadyExistsException extends MedicalClinicException {
    public PatientAlreadyExistsException(String idCardNo) {
        super("Patient with id card number " + idCardNo + " already exists", HttpStatus.CONFLICT);
    }
}
