package com.andrzej_kosmowski.facility;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FacilityMapper {
    Facility toDomain(FacilityEntity facility);
    FacilityEntity toEntity(Facility facility);
    FacilityDto toDto(Facility facility);
}
