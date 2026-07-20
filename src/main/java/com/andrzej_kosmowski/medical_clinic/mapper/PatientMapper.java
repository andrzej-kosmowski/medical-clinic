package com.andrzej_kosmowski.medical_clinic.mapper;

import com.andrzej_kosmowski.medical_clinic.dto.CreatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.PatientDto;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient from(CreatePatientCommand command);
    PatientDto toDto(Patient patient);
}