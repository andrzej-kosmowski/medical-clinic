package com.andrzej_kosmowski.doctor;

import com.andrzej_kosmowski.facility.FacilityEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "facilityIds", source = "facilities")
    Doctor toDomain(DoctorEntity entity);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "facilities", ignore = true)
    DoctorEntity toEntity(Doctor doctor);
    DoctorDto toDto(Doctor doctor);

    default Long toId(FacilityEntity facility) {
        return facility == null ? null : facility.getId();
    }
}
