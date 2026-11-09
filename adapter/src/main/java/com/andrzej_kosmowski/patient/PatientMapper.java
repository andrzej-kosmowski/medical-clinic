package com.andrzej_kosmowski.patient;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(target = "userId", source = "user.id")
    Patient toDomain(PatientEntity entity);

    @Mapping(target = "user", ignore = true)
    PatientEntity toEntity(Patient patient);
    PatientDto toDto(Patient patient);
}
