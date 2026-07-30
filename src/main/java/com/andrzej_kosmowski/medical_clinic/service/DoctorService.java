package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.user.CreateUserCommand;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.AssignFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.DoctorDto;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.andrzej_kosmowski.medical_clinic.dto.facility.FacilityDto;
import com.andrzej_kosmowski.medical_clinic.exception.doctor.DoctorNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.DoctorMapper;
import com.andrzej_kosmowski.medical_clinic.mapper.FacilityMapper;
import com.andrzej_kosmowski.medical_clinic.model.Doctor;
import com.andrzej_kosmowski.medical_clinic.model.Facility;
import com.andrzej_kosmowski.medical_clinic.model.User;
import com.andrzej_kosmowski.medical_clinic.repository.DoctorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;
    private final DoctorMapper doctorMapper;
    private final FacilityMapper facilityMapper;
    private final UserService userService;
    private final FacilityService facilityService;

    public List<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    public DoctorDto getDoctorByEmail(String email) {
        Doctor doctor = findDoctorOrThrow(email);
        return doctorMapper.toDto(doctor);
    }

    public List<FacilityDto> getFacilities(String email) {
        Doctor doctor = findDoctorOrThrow(email);
        return doctor.getFacilities().stream()
                .map(facilityMapper::toDto)
                .toList();
    }

    @Transactional
    public DoctorDto addDoctor(CreateDoctorCommand command) {
        CreateUserCommand userCommand = new CreateUserCommand(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.password()
        );
        User user = userService.createUser(userCommand);
        Doctor doctor = new Doctor(command.specialization());
        doctor.assignUser(user);
        doctor.validate();
        Doctor saved  = doctorRepository.save(doctor);
        Optional.ofNullable(command.facilityNames())
                .orElse(Set.of())
                .forEach(name -> {
                    Facility facility = facilityService.getByName(name);
                    doctor.assignFacility(facility);
                });
        return doctorMapper.toDto(saved);
    }

    @Transactional
    public DoctorDto updateDoctorByEmail(String email, UpdateDoctorCommand command) {
        Doctor doctor = findDoctorOrThrow(email);
        userService.validateEmailChange(
                doctor.getUser(),
                command.email());
        doctor.updateSpecialization(command.specialization());
        doctor.getUser().update(
                command.firstName(),
                command.lastName(),
                command.email()
        );
        return doctorMapper.toDto(doctor);
    }

    @Transactional
    public void deleteDoctorByEmail(String email) {
        Doctor doctor = findDoctorOrThrow(email);
        doctorRepository.delete(doctor);
    }

    @Transactional
    public DoctorDto assignFacility(String email, AssignFacilityCommand command) {
        Doctor doctor = findDoctorOrThrow(email);
        Facility facility = facilityService.getByName(command.facilityName());
        doctor.assignFacility(facility);
        return doctorMapper.toDto(doctor);
    }

    @Transactional
    public void removeFacility(String email, String facilityName) {
        Doctor doctor = findDoctorOrThrow(email);
        Facility facility = facilityService.getByName(facilityName);
        doctor.removeFacility(facility);
    }

    private Doctor findDoctorOrThrow(String email) {
        return doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new DoctorNotFoundException(email));
    }
}
