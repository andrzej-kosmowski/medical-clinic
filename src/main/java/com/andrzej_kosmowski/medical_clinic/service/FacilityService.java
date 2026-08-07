package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityService {
    private final FacilityRepository facilityRepository;
    private final FacilityMapper facilityMapper;
    private final DoctorMapper doctorMapper;

    public PageResponse<FacilityDto> getAllFacilities(Pageable pageable) {
        return PageResponse.from(facilityRepository.findAll(pageable)
                .map(facilityMapper::toDto));
    }

    public FacilityDto getFacilityById(Long id) {
        Facility facility = findFacilityOrThrow(id);
        return facilityMapper.toDto(facility);
    }

    public List<DoctorDto> getDoctors(Long id) {
        Facility facility = findFacilityOrThrow(id);
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
    public FacilityDto updateFacility(Long id,UpdateFacilityCommand command) {
        Facility facility = findFacilityOrThrow(id);
        if (!facility.getName().equals(command.name())
                && facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        facility.update(command);
        return facilityMapper.toDto(facility);
    }

    @Transactional
    public void deleteFacility(Long id) {
        Facility facility = findFacilityOrThrow(id);
        facility.getDoctors()
                    .forEach(doctor -> doctor.getFacilities().remove(facility));
        facility.getDoctors().clear();
        facilityRepository.delete(facility);
    }

    public Facility getById(Long id) {
        return findFacilityOrThrow(id);
    }

    public List<Facility> getAllByIds(Set<Long> ids) {
        List<Facility> facilities = facilityRepository.findAllById(ids);
        validateFacilitiesExist(ids, facilities);
        return facilities;
    }

    private void validateFacilitiesExist(Set<Long> requestedIds, List<Facility> facilities) {
        Set<Long> foundIds = facilities.stream()
                .map(Facility::getId)
                .collect(Collectors.toSet());
        Set<Long> missingIds = requestedIds.stream()
                .filter(name -> !foundIds.contains(name))
                .collect(Collectors.toSet());
        if (!missingIds.isEmpty()) {
            throw new FacilityNotFoundException(missingIds);
        }
    }

    private Facility findFacilityOrThrow(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException(id));
    }
}
