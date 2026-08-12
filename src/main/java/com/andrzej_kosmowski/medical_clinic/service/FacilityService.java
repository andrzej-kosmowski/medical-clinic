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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
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
        log.info("Creating facility: name={}", command.name());
        if (facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        Facility facility = facilityMapper.from(command);
        facility.validate();
        Facility saved = facilityRepository.save(facility);
        log.info("Facility created successfully: id={}, name={}", saved.getId(), saved.getName());
        return facilityMapper.toDto(saved);
    }

    @Transactional
    public FacilityDto updateFacility(Long id,UpdateFacilityCommand command) {
        Facility facility = findFacilityOrThrow(id);
        log.info("Updating facility: id={}, name={}", facility.getId(), facility.getName());
        if (!facility.getName().equals(command.name())
                && facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        facility.update(command);
        log.info("Facility updated successfully: id={}, name={}", facility.getId(), facility.getName());
        return facilityMapper.toDto(facility);
    }

    @Transactional
    public void deleteFacility(Long id) {
        Facility facility = findFacilityOrThrow(id);
        log.info("Deleting facility: id={}, name={}, doctorsCount={}",
                facility.getId(), facility.getName(), facility.getDoctors().size());
        facility.getDoctors()
                    .forEach(doctor -> doctor.getFacilities().remove(facility));
        facility.getDoctors().clear();
        facilityRepository.delete(facility);
        log.info("Facility deleted successfully: id={}, name={}", id, facility.getName());
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
