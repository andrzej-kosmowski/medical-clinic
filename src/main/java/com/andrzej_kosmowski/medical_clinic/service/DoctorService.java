package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.*;
import com.andrzej_kosmowski.medical_clinic.exception.DoctorNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.DoctorMapper;
import com.andrzej_kosmowski.medical_clinic.mapper.FacilityMapper;
import com.andrzej_kosmowski.medical_clinic.model.Doctor;
import com.andrzej_kosmowski.medical_clinic.model.Facility;
import com.andrzej_kosmowski.medical_clinic.model.User;
import com.andrzej_kosmowski.medical_clinic.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public FacilityDto getFacility(String email) {
        Doctor doctor = findDoctorOrThrow(email);
        return facilityMapper.toDto(doctor.getFacility());
    }

    public List<DoctorDto> getDoctorsByFacility(String facilityName) {
        return doctorRepository.findAllByFacilityName(facilityName)
                .stream()
                .map(doctorMapper::toDto)
                .toList();
    }

    public DoctorDto addDoctor(CreateDoctorCommand command) {
        User user = userService.createUser(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.password()
        );
        Doctor doctor = new Doctor(command.specialization());
        doctor.validate();
        doctor.assignUser(user);
        if (command.facilityName() != null) {
            Facility facility = facilityService.getByName(command.facilityName());
            doctor.assignFacility(facility);
        }
        Doctor saved  = doctorRepository.save(doctor);
        return doctorMapper.toDto(saved);
    }

    public DoctorDto updateDoctorByEmail(String email,UpdateDoctorCommand command) {
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
        Doctor updated = doctorRepository.save(doctor);
        return doctorMapper.toDto(updated);
    }

    public void deleteDoctorByEmail(String email) {
        Doctor doctor = findDoctorOrThrow(email);
        doctorRepository.delete(doctor);
    }

    public DoctorDto assignFacility(String email, AssignFacilityCommand command) {
        Doctor doctor = findDoctorOrThrow(email);
        Facility facility = facilityService.getByName(command.facilityName());
        doctor.assignFacility(facility);
        Doctor assigned = doctorRepository.save(doctor);
        return doctorMapper.toDto(assigned);
    }

    public void removeFacility(String email) {
        Doctor doctor = findDoctorOrThrow(email);
        doctor.removeFacility();
        doctorRepository.save(doctor);
    }

    private Doctor findDoctorOrThrow(String email) {
        return doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new DoctorNotFoundException(email));
    }
}
