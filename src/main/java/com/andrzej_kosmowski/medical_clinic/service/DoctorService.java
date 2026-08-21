package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.AssignFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.DoctorDto;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.andrzej_kosmowski.medical_clinic.dto.facility.FacilityDto;
import com.andrzej_kosmowski.medical_clinic.exception.doctor.DoctorNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.DoctorMapper;
import com.andrzej_kosmowski.medical_clinic.mapper.FacilityMapper;
import com.andrzej_kosmowski.medical_clinic.mapper.UserMapper;
import com.andrzej_kosmowski.medical_clinic.model.Doctor;
import com.andrzej_kosmowski.medical_clinic.model.Facility;
import com.andrzej_kosmowski.medical_clinic.model.User;
import com.andrzej_kosmowski.medical_clinic.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final FacilityMapper facilityMapper;
    private final UserMapper userMapper;
    private final UserService userService;
    private final FacilityService facilityService;

    public PageResponse<DoctorDto> getAllDoctors(Pageable pageable) {
        return PageResponse.from(doctorRepository.findAll(pageable)
                .map(doctorMapper::toDto));
    }

    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = findDoctorOrThrow(id);
        return doctorMapper.toDto(doctor);
    }

    public List<FacilityDto> getFacilities(Long id) {
        Doctor doctor = findDoctorOrThrow(id);
        return doctor.getFacilities().stream()
                .map(facilityMapper::toDto)
                .toList();
    }

    @Transactional
    public DoctorDto addDoctor(CreateDoctorCommand command) {
        log.info("Creating doctor: specialization={}, email={}", command.specialization(), command.email());
        User user = userService.createUser(userMapper.toUserCommand(command));
        Doctor doctor = Doctor.create(command.specialization(), user);
        Doctor saved  = doctorRepository.save(doctor);
        Set<Long> facilityIds = Optional.ofNullable(command.facilityIds())
                .orElse(Set.of());
        List<Facility> facilities = facilityService.getAllByIds(facilityIds);
        facilities.forEach(doctor::assignFacility);
        log.info("Doctor created successfully: doctorId={}, specialization={}, facilitiesCount={}",
                saved.getId(), saved.getSpecialization(), facilities.size());
        return doctorMapper.toDto(saved);
    }

    @Transactional
    public DoctorDto updateDoctor(Long id, UpdateDoctorCommand command) {
        Doctor doctor = findDoctorOrThrow(id);
        log.info("Updating doctor: doctorId={}, specialization={}", id, doctor.getSpecialization());
        userService.validateEmailChange(doctor.getUser(), command.email());
        doctor.updateSpecialization(command.specialization());
        doctor.getUser().update(command.firstName(), command.lastName(), command.email());
        log.info("Doctor updated successfully: doctorId={}", id);
        return doctorMapper.toDto(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = findDoctorOrThrow(id);
        log.info("Deleting doctor: doctorId={}, specialization={}", doctor.getId(), doctor.getSpecialization());
        doctorRepository.delete(doctor);
        log.info("Doctor deleted successfully: doctorId={}", id);

    }

    @Transactional
    public DoctorDto assignFacility(Long id, AssignFacilityCommand command) {
        Doctor doctor = findDoctorOrThrow(id);
        Facility facility = facilityService.getById(command.facilityId());
        doctor.assignFacility(facility);
        log.info("Facility assigned to doctor: doctorId={}, facilityId={}", id, command.facilityId());
        return doctorMapper.toDto(doctor);
    }

    @Transactional
    public void removeFacility(Long doctorId, Long facilityId) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        Facility facility = facilityService.getById(facilityId);
        doctor.removeFacility(facility);
        log.info("Facility removed from doctor: doctorId={}, facilityId={}", doctorId, facilityId);
    }

    private Doctor findDoctorOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }
}
