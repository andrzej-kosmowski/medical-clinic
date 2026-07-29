package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.CreateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.dto.FacilityDto;
import com.andrzej_kosmowski.medical_clinic.dto.UpdateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.exception.FacilityAlreadyExistsException;
import com.andrzej_kosmowski.medical_clinic.exception.FacilityNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.FacilityMapper;
import com.andrzej_kosmowski.medical_clinic.model.Facility;
import com.andrzej_kosmowski.medical_clinic.repository.FacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;

    public List<FacilityDto> getAllFacilities() {
        return facilityRepository.findAll().stream()
                .map(facilityMapper::toDto)
                .toList();
    }

    public FacilityDto getFacilityByName(String name) {
        Facility facility = findFacilityOrThrow(name);
        return facilityMapper.toDto(facility);
    }

    public FacilityDto addFacility(CreateFacilityCommand command) {
        if (facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        Facility facility = facilityMapper.from(command);
        facility.validate();
        Facility saved = facilityRepository.save(facility);
        return facilityMapper.toDto(saved);
    }

    public FacilityDto updateFacility(String name,UpdateFacilityCommand command) {
        Facility facility = findFacilityOrThrow(name);
        if (!facility.getName().equals(command.name())
                && facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        facility.update(command);
        Facility updated = facilityRepository.save(facility);
        return facilityMapper.toDto(updated);
    }

    public void deleteFacilityByName(String name) {
        Facility facility = findFacilityOrThrow(name);
        facilityRepository.delete(facility);
    }

    public Facility getByName(String name) {
        return findFacilityOrThrow(name);
    }

    private Facility findFacilityOrThrow(String name) {
        return facilityRepository.findByName(name)
                .orElseThrow(() -> new FacilityNotFoundException(name));
    }
}
