package com.andrzej_kosmowski.visit;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VisitMapper {
    @Mapping(target = "doctorId", source = "doctor.id")
    @Mapping(target = "patientId", source = "patient.id")
    Visit toDomain(VisitEntity entity);

    @Mapping(target = "doctor", ignore = true)
    @Mapping(target = "patient", ignore = true)
    VisitEntity toEntity(Visit visit);
    VisitDto toDto(Visit visit);
}
