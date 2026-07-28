package com.andrzej_kosmowski.medical_clinic.mapper;

import com.andrzej_kosmowski.medical_clinic.dto.CreatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.PatientDto;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient from(CreatePatientCommand command);

    @Mapping(target = "email", source = "user.email")
    PatientDto toDto(Patient patient);
}