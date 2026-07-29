package com.andrzej_kosmowski.medical_clinic.mapper;

import com.andrzej_kosmowski.medical_clinic.dto.CreateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.dto.FacilityDto;
import com.andrzej_kosmowski.medical_clinic.model.Facility;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    Facility from(CreateFacilityCommand command);
    FacilityDto toDto(Facility facility);
}
