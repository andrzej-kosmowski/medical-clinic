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
import com.andrzej_kosmowski.medical_clinic.model.Doctor;
import com.andrzej_kosmowski.medical_clinic.model.Facility;
import com.andrzej_kosmowski.medical_clinic.repository.FacilityRepository;
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

class FacilityServiceTest {
    FacilityService facilityService;
    FacilityRepository facilityRepository;
    FacilityMapper facilityMapper;
    DoctorMapper doctorMapper;

    @BeforeEach
    void setUp() {
        this.facilityRepository = Mockito.mock(FacilityRepository.class);
        this.doctorMapper = Mappers.getMapper(DoctorMapper.class);
        this.facilityMapper = Mappers.getMapper(FacilityMapper.class);
        ReflectionTestUtils.setField(doctorMapper, "facilityMapper", facilityMapper);
        this.facilityService = new FacilityService(facilityRepository, facilityMapper, doctorMapper);
    }

    @Test
    void getAllFacilities_FacilitiesExist_FacilitiesReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Facility> facilities = new ArrayList<>();
        facilities.add(new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10"
        ));
        facilities.add(new Facility("Health Clinic", "Cracow", "30-001",
                "Long Street", "20A"
        ));
        Page<Facility> page = new PageImpl<>(facilities);
        when(facilityRepository.findAll(pageable)).thenReturn(page);
        // when
        PageResponse<FacilityDto> result = facilityService.getAllFacilities(pageable);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Medical Center", result.content().get(0).name()),
                () -> assertEquals("Health Clinic", result.content().get(1).name()),
                () -> assertEquals("Warsaw", result.content().get(0).city()),
                () -> assertEquals("Cracow", result.content().get(1).city()),
                () -> assertEquals("00-001", result.content().get(0).zipCode()),
                () -> assertEquals("30-001", result.content().get(1).zipCode()),
                () -> assertEquals("Main Street", result.content().get(0).street()),
                () -> assertEquals("Long Street", result.content().get(1).street()),
                () -> assertEquals("10", result.content().get(0).buildingNumber()),
                () -> assertEquals("20A", result.content().get(1).buildingNumber())
        );
    }

    @Test
    void getFacilityById_FacilityExist_FacilityReturned() {
        // given
        Facility facility = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        // when
        FacilityDto result = facilityService.getFacilityById(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals("Medical Center", result.name()),
                () -> assertEquals("Warsaw", result.city()),
                () -> assertEquals("00-001", result.zipCode()),
                () -> assertEquals("Main Street", result.street()),
                () -> assertEquals("10", result.buildingNumber())
        );
    }

    @Test
    void getFacilityById_FacilityDoesNotExist_ThrowsException() {
        // given
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class,
                () -> facilityService.getFacilityById(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void getDoctors_FacilitiesExists_DoctorsReturned() {
        // given
        Facility facility = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        Doctor doctor1 = new Doctor("Cardiologist");
        Doctor doctor2 = new Doctor("Dentist");
        facility.assignDoctor(doctor1);
        facility.assignDoctor(doctor2);
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        // when
        List<DoctorDto> result = facilityService.getDoctors(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Cardiologist", result.get(0).specialization()),
                () -> assertEquals("Dentist", result.get(1).specialization())
        );
    }

    @Test
    void getDoctors_FacilityNotFound_ThrowsException() {
        // given
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class,
                () -> facilityService.getDoctors(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void addFacility_ValidCommand_FacilityCreated() {
        // given
        CreateFacilityCommand command = new CreateFacilityCommand("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        when(facilityRepository.existsByName("Medical Center")).thenReturn(false);
        when(facilityRepository.save(any(Facility.class))).thenAnswer(i -> i.getArgument(0));
        // when
        FacilityDto result = facilityService.addFacility(command);
        // then
        Assertions.assertAll(
                () -> assertEquals("Medical Center", result.name()),
                () -> assertEquals("Warsaw", result.city()),
                () -> assertEquals("00-001", result.zipCode()),
                () -> assertEquals("Main Street", result.street()),
                () -> assertEquals("10", result.buildingNumber())
        );
        verify(facilityRepository).save(any(Facility.class));
    }

    @Test
    void addFacility_NameAlreadyExists_ThrowsException() {
        // given
        CreateFacilityCommand command = new CreateFacilityCommand("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        when(facilityRepository.existsByName("Medical Center")).thenReturn(true);
        // when
        FacilityAlreadyExistsException exception = assertThrows(FacilityAlreadyExistsException.class,
                () -> facilityService.addFacility(command));
        // then
        Assertions.assertAll(
                () -> assertEquals("Facility with name Medical Center already exists", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void updateFacility_ValidCommand_FacilityUpdated() {
        // given
        Facility facility = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        UpdateFacilityCommand command = new UpdateFacilityCommand("New", "Cracow", "30-001",
                "Long Street", "20");
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        // when
        FacilityDto result = facilityService.updateFacility(1L, command);
        // then
        Assertions.assertAll(
                () -> assertEquals("New", result.name()),
                () -> assertEquals("Cracow", result.city()),
                () -> assertEquals("30-001", result.zipCode()),
                () -> assertEquals("Long Street", result.street()),
                () -> assertEquals("20", result.buildingNumber())
        );
    }

    @Test
    void updateFacility_FacilityDoesNotExist_ThrowsException() {
        // given
        UpdateFacilityCommand command = new UpdateFacilityCommand("New", "Cracow", "30-001",
                "Long Street", "20");
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class,
                () -> facilityService.updateFacility(1L, command));
        // then
        Assertions.assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void updateFacility_NameAlreadyExists_ThrowsException() {
        // given
        Facility facility = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        UpdateFacilityCommand command = new UpdateFacilityCommand("New", "Cracow", "30-001",
                "Long Street", "20");
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityRepository.existsByName("New")).thenReturn(true);
        // when
        FacilityAlreadyExistsException exception = assertThrows(FacilityAlreadyExistsException.class,
                () -> facilityService.updateFacility(1L, command));
        // then
        Assertions.assertAll(
                () -> assertEquals("Facility with name New already exists", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void deleteFacility_FacilityExists_FacilityDeleted() {
        // given
        Facility facility = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        Doctor doctor = new Doctor("Cardiologist");
        facility.assignDoctor(doctor);
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        // when
        facilityService.deleteFacility(1L);
        // then
        Assertions.assertAll(
                () -> assertTrue(facility.getDoctors().isEmpty()),
                () -> assertTrue(doctor.getFacilities().isEmpty())
        );
        verify(facilityRepository).delete(facility);
    }

    @Test
    void deleteFacility_FacilityDoesNotExist_ThrowsException() {
        // given
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class,
                () -> facilityService.deleteFacility(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void getById_FacilityExists_FacilityFound() {
        // given
        Facility facility = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        // when
        Facility result = facilityService.getById(1L);
        Assertions.assertAll(
                () -> assertEquals("Medical Center", result.getName()),
                () -> assertEquals("Warsaw", result.getCity()),
                () -> assertEquals("00-001", result.getZipCode()),
                () -> assertEquals("Main Street", result.getStreet()),
                () -> assertEquals("10", result.getBuildingNumber())
        );
    }

    @Test
    void getById_FacilityDoesNotExist_ThrowsException() {
        // given
        when(facilityRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class,
                () -> facilityService.getById(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Facility with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void getAllByIds_AllFacilitiesExist_FacilitiesReturned() {
        // given
        Set<Long> ids = Set.of(1L, 2L);
        Facility facility1 = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        Facility facility2 = new Facility("Health Clinic", "Cracow", "30-001",
                "Long Street", "20A");
        ReflectionTestUtils.setField(facility1, "id", 1L);
        ReflectionTestUtils.setField(facility2, "id", 2L);
        when(facilityRepository.findAllById(ids)).thenReturn(List.of(facility1, facility2));
        // when
        List<Facility> result = facilityService.getAllByIds(ids);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Medical Center", result.get(0).getName()),
                () -> assertEquals("Health Clinic", result.get(1).getName()),
                () -> assertEquals("Warsaw", result.get(0).getCity()),
                () -> assertEquals("Cracow", result.get(1).getCity()),
                () -> assertEquals("00-001", result.get(0).getZipCode()),
                () -> assertEquals("30-001", result.get(1).getZipCode()),
                () -> assertEquals("Main Street", result.get(0).getStreet()),
                () -> assertEquals("Long Street", result.get(1).getStreet()),
                () -> assertEquals("10", result.get(0).getBuildingNumber()),
                () -> assertEquals("20A", result.get(1).getBuildingNumber())
        );
    }

    @Test
    void getAllByIds_FacilityNotFound_ThrowsException() {
        // given
        Set<Long> ids = Set.of(1L, 2L);
        Facility facility = new Facility("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        ReflectionTestUtils.setField(facility, "id", 1L);
        when(facilityRepository.findAllById(ids)).thenReturn(List.of(facility));
        // when
        FacilityNotFoundException exception = assertThrows(FacilityNotFoundException.class,
                () -> facilityService.getAllByIds(ids));
        // then
        Assertions.assertAll(
                () -> assertEquals("Facility with id 2 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }
}