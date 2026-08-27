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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DoctorServiceTest {
    DoctorService doctorService;
    DoctorRepository doctorRepository;
    DoctorMapper doctorMapper;
    FacilityMapper facilityMapper;
    UserMapper userMapper;
    UserService userService;
    FacilityService facilityService;

    @BeforeEach
    void setUp() {
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        this.facilityMapper = Mappers.getMapper(FacilityMapper.class);
        ReflectionTestUtils.setField(doctorMapper, "facilityMapper", facilityMapper);
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.userService = Mockito.mock(UserService.class);
        this.facilityService = Mockito.mock(FacilityService.class);
        this.doctorService = new DoctorService(doctorRepository, doctorMapper, facilityMapper, userMapper,
                userService, facilityService);
    }

    @Test
    void getAllDoctors_DoctorsExist_DoctorsReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor("Cardiologist"));
        doctors.add(new Doctor("Dentist"));
        Page<Doctor> page = new PageImpl<>(doctors);
        when(doctorRepository.findAll(pageable)).thenReturn(page);
        // when
        PageResponse<DoctorDto> result = doctorService.getAllDoctors(pageable);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Cardiologist", result.content().get(0).specialization()),
                () -> assertEquals("Dentist", result.content().get(1).specialization())
        );
    }

    @Test
    void getDoctorById_DoctorExists_DoctorReturned() {
        // given
        Doctor doctor = new Doctor("Cardiologist");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        // when
        DoctorDto result = doctorService.getDoctorById(1L);
        // then
        assertEquals("Cardiologist", result.specialization());
    }

    @Test
    void getDoctorById_DoctorDoesNotExist_ThrowsException() {
        // given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.getDoctorById(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void getFacilities_DoctorExists_FacilitiesReturned() {
        // given
        Doctor doctor = new Doctor("Cardiologist");
        Facility facility1 = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        Facility facility2 = new Facility("Health Clinic", "Cracow", "30-001",
                "Long Street", "20A");
        doctor.assignFacility(facility1);
        doctor.assignFacility(facility2);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        // when
        List<FacilityDto> result = doctorService.getFacilities(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Medical Center", result.get(0).name()),
                () -> assertEquals("Health Clinic", result.get(1).name()),
                () -> assertEquals("Warsaw", result.get(0).city()),
                () -> assertEquals("Cracow", result.get(1).city()),
                () -> assertEquals("Main Street", result.get(0).street()),
                () -> assertEquals("Long Street", result.get(1).street())
        );
    }

    @Test
    void getFacilities_DoctorDoesNotExist_ThrowsException() {
        // given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.getFacilities(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void addDoctor_ValidCommand_DoctorCreated() {
        // given
        CreateDoctorCommand command = new CreateDoctorCommand("doctor@test.pl", "pass123",
                "Jan", "Kowalski", "Cardiologist", Set.of(1L, 2L));
        User user = new User("Jan", "Kowalski", "doctor@test.pl", "pass123");
        Facility facility1 = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        Facility facility2 = new Facility("Health Clinic", "Cracow", "30-001",
                "Long Street", "20A");
        when(userService.createUser(any())).thenReturn(user);
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(i -> i.getArgument(0));
        when(facilityService.getAllByIds(Set.of(1L, 2L))).thenReturn(List.of(facility1, facility2));
        // when
        DoctorDto result = doctorService.addDoctor(command);
        // then
        Assertions.assertAll(
                () -> assertEquals("Cardiologist", result.specialization()),
                () -> assertEquals("doctor@test.pl", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertEquals(2, result.facilities().size())
        );
        verify(userService).createUser(any());
        verify(doctorRepository).save(any(Doctor.class));
        verify(facilityService).getAllByIds(Set.of(1L, 2L));
    }

    @Test
    void updateDoctor_ValidCommand_DoctorUpdated() {
        // given
        User user = new User("Jan", "Kowalski", "doctor@test.pl", "pass123");
        Doctor doctor = Doctor.create("Cardiologist", user);
        UpdateDoctorCommand command = new UpdateDoctorCommand("new@test.pl", "Adam",
                "Nowak", "Dentist");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        // when
        DoctorDto result = doctorService.updateDoctor(1L, command);
        // then
        Assertions.assertAll(
                () -> assertEquals("Dentist", result.specialization()),
                () -> assertEquals("new@test.pl", result.email()),
                () -> assertEquals("Adam", result.firstName()),
                () -> assertEquals("Nowak", result.lastName())
        );
    }

    @Test
    void updateDoctor_DoctorDoesNotExist_ThrowsException() {
        // given
        UpdateDoctorCommand command = new UpdateDoctorCommand("new@test.pl", "Adam",
                "Nowak", "Dentist");
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.updateDoctor(1L, command));
        // then
        Assertions.assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void deleteDoctor_DoctorExists_DoctorDeleted() {
        // given
        Doctor doctor = new Doctor("Cardiologist");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        // when
        doctorService.deleteDoctor(1L);
        // then
        verify(doctorRepository).delete(doctor);
    }

    @Test
    void deleteDoctor_DoctorDoesNotExist_ThrowsException() {
        // given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.deleteDoctor(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void assignFacility_DoctorAndFacilityExist_FacilityAssigned() {
        // given
        Doctor doctor = new Doctor("Cardiologist");
        Facility facility = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        AssignFacilityCommand command = new AssignFacilityCommand(1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityService.getById(1L)).thenReturn(facility);
        // when
        DoctorDto result = doctorService.assignFacility(1L, command);
        // then
        Assertions.assertAll(
                () -> assertEquals(1, result.facilities().size()),
                () -> assertTrue(result.facilities().stream()
                        .anyMatch(f -> f.name().equals("Medical Center")))
        );
    }

    @Test
    void assignFacility_DoctorDoesNotExist_ThrowsException() {
        // given
        AssignFacilityCommand command = new AssignFacilityCommand(1L);
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.assignFacility(1L, command));
        // then
        Assertions.assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void removeFacility_DoctorAndFacilityExist_FacilityRemoved() {
        // given
        Doctor doctor = new Doctor("Cardiologist");
        Facility facility = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        doctor.assignFacility(facility);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(facilityService.getById(1L)).thenReturn(facility);
        // when
        doctorService.removeFacility(1L, 1L);
        // then
        assertTrue(doctor.getFacilities().isEmpty());
        verify(facilityService).getById(1L);
    }

    @Test
    void removeFacility_DoctorDoesNotExist_ThrowsException() {
        // given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> doctorService.removeFacility(1L, 1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }
}