package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.doctor.DoctorDto;
import com.andrzej_kosmowski.medical_clinic.dto.facility.CreateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.dto.facility.FacilityDto;
import com.andrzej_kosmowski.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.exception.facility.FacilityAlreadyExistsException;
import com.andrzej_kosmowski.medical_clinic.exception.facility.FacilityNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.DoctorMapper;
import com.andrzej_kosmowski.medical_clinic.mapper.FacilityMapper;
import com.andrzej_kosmowski.medical_clinic.model.Facility;
import com.andrzej_kosmowski.medical_clinic.repository.FacilityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;
    private final DoctorMapper doctorMapper;

    public List<FacilityDto> getAllFacilities() {
        return facilityRepository.findAll().stream()
                .map(facilityMapper::toDto)
                .toList();
    }

    public FacilityDto getFacilityByName(String name) {
        Facility facility = findFacilityOrThrow(name);
        return facilityMapper.toDto(facility);
    }

    public List<DoctorDto> getDoctors(String facilityName) {
        Facility facility = findFacilityOrThrow(facilityName);
        return facility.getDoctors().stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    @Transactional
    public FacilityDto addFacility(CreateFacilityCommand command) {
        if (facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        Facility facility = facilityMapper.from(command);
        facility.validate();
        Facility saved = facilityRepository.save(facility);
        return facilityMapper.toDto(saved);
    }

    @Transactional
    public FacilityDto updateFacility(String name,UpdateFacilityCommand command) {
        Facility facility = findFacilityOrThrow(name);
        if (!facility.getName().equals(command.name())
                && facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        facility.update(command);
        return facilityMapper.toDto(facility);
    }

    @Transactional
    public void deleteFacilityByName(String name) {
        Facility facility = findFacilityOrThrow(name);
        facility.getDoctors()
                    .forEach(doctor -> doctor.getFacilities().remove(facility));
        facility.getDoctors().clear();
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
