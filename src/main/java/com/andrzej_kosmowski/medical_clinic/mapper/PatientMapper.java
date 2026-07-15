package com.andrzej_kosmowski.medical_clinic.mapper;

import com.andrzej_kosmowski.medical_clinic.dto.CreatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.PatientDto;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient from(CreatePatientCommand command) {
        return new Patient(
                command.email(),
                command.password(),
                command.idCardNo(),
                command.firstName(),
                command.lastName(),
                command.phoneNumber(),
                command.birthday()
        );
    }

    public PatientDto toDto(Patient patient) {
        return new PatientDto(
                patient.getEmail(),
                patient.getIdCardNo(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getPhoneNumber(),
                patient.getBirthday()
        );
    }

}
