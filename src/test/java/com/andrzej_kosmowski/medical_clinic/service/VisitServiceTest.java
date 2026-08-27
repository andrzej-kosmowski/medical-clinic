package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.visit.CreateVisitCommand;
import com.andrzej_kosmowski.medical_clinic.dto.visit.VisitDto;
import com.andrzej_kosmowski.medical_clinic.exception.doctor.DoctorNotFoundException;
import com.andrzej_kosmowski.medical_clinic.exception.patient.PatientNotFoundException;
import com.andrzej_kosmowski.medical_clinic.exception.visit.*;
import com.andrzej_kosmowski.medical_clinic.mapper.VisitMapper;
import com.andrzej_kosmowski.medical_clinic.model.Doctor;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import com.andrzej_kosmowski.medical_clinic.model.Visit;
import com.andrzej_kosmowski.medical_clinic.repository.DoctorRepository;
import com.andrzej_kosmowski.medical_clinic.repository.PatientRepository;
import com.andrzej_kosmowski.medical_clinic.repository.VisitRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class VisitServiceTest {
    VisitService visitService;
    VisitRepository visitRepository;
    DoctorRepository doctorRepository;
    PatientRepository patientRepository;
    VisitMapper visitMapper;

    @BeforeEach
    void setUp() {
        this.visitRepository = Mockito.mock(VisitRepository.class);
        this.doctorRepository = Mockito.mock(DoctorRepository.class);
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.visitMapper = Mappers.getMapper(VisitMapper.class);
        this.visitService = new VisitService(visitRepository, doctorRepository, patientRepository, visitMapper);
    }

    @Test
    void getAllVisits_VisitsExist_VisitsReturned() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(1)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endTime = startTime.plusHours(1);
        Pageable pageable = PageRequest.of(0, 10);
        Doctor doctor = new Doctor("Cardiologist");
        Visit visit1 = new Visit(startTime, endTime, doctor);
        Visit visit2 = new Visit(startTime.plusHours(2), endTime.plusHours(2), doctor);
        List<Visit> visits = new ArrayList<>();
        visits.add(visit1);
        visits.add(visit2);
        Page<Visit> visitPage = new PageImpl<>(visits);
        when(visitRepository.findAll(pageable)).thenReturn(visitPage);
        // when
        PageResponse<VisitDto> result = visitService.getAllVisits(pageable);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(startTime, result.content().get(0).startTime()),
                () -> assertEquals(startTime.plusHours(2), result.content().get(1).startTime())
        );
    }

    @Test
    void getVisitById_VisitExist_VisitReturned() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(1)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endTime = startTime.plusHours(1);
        Doctor doctor = new Doctor("Cardiologist");
        Visit visit = new Visit(startTime, endTime, doctor);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        // when
        VisitDto result = visitService.getVisitById(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(startTime, result.startTime()),
                () -> assertEquals(startTime.plusHours(1), result.endTime())
        );
    }

    @Test
    void getVisitById_VisitNotFound_ThrowsException() {
        // given
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        VisitNotFoundException exception = assertThrows(VisitNotFoundException.class,
                () -> visitService.getVisitById(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void getAvailableVisits_AvailableVisitsExist_VisitsReturned() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endTime = startTime.plusHours(1);
        Doctor doctor = new Doctor("Cardiologist");
        Visit visit1 = new Visit(startTime, endTime, doctor);
        Visit visit2 = new Visit(startTime.plusHours(2), endTime.plusHours(2), doctor);
        when(visitRepository.findAllByPatientIsNullAndStartTimeAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(visit1, visit2));
        // when
        List<VisitDto> result = visitService.getAvailableVisits();
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(startTime, result.get(0).startTime()),
                () -> assertEquals(startTime.plusHours(2), result.get(1).startTime())
        );
    }

    @Test
    void getPatientVisits_PatientExists_VisitsReturned() {
        // given
        Patient patient = new Patient("ABC123", "333444555",
                LocalDate.of(1990, 1, 1));
        Doctor doctor = new Doctor("Cardiologist");
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        Visit visit1 = new Visit(startTime, startTime.plusHours(1), doctor);
        Visit visit2 = new Visit(startTime.plusDays(1), startTime.plusDays(1).plusHours(1), doctor);
        visit1.assignPatient(patient);
        visit2.assignPatient(patient);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        // when
        List<VisitDto> result = visitService.getPatientVisits(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(startTime, result.get(0).startTime()),
                () -> assertEquals(startTime.plusDays(1), result.get(1).startTime())
        );
    }

    @Test
    void getPatientVisits_PatientNotFound_ThrowsException() {
        // given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> visitService.getPatientVisits(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Patient with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void getDoctorVisits_DoctorExists_VisitsReturned() {
        // given
        Doctor doctor = new Doctor("Cardiologist");
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        Visit visit1 = new Visit(startTime, startTime.plusHours(1), doctor);
        Visit visit2 = new Visit(startTime.plusDays(1), startTime.plusDays(1).plusHours(1), doctor);
        doctor.addVisit(visit1);
        doctor.addVisit(visit2);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        // when
        List<VisitDto> result = visitService.getDoctorVisits(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(startTime, result.get(0).startTime()),
                () -> assertEquals(startTime.plusDays(1), result.get(1).startTime())
        );
    }

    @Test
    void getDoctorVisits_DoctorNotFound_ThrowsException() {
        // given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> visitService.getDoctorVisits(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void createVisit_ValidCommand_VisitCreated() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endTime = startTime.plusHours(1);
        CreateVisitCommand command = new CreateVisitCommand(1L, startTime, endTime);
        Doctor doctor = new Doctor("Cardiologist");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(visitRepository.existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        1L, endTime, startTime)).thenReturn(false);
        when(visitRepository.save(any(Visit.class))).thenAnswer(i -> i.getArgument(0));
        // when
        VisitDto result = visitService.createVisit(command);
        // then
        Assertions.assertAll(
                () -> assertEquals(startTime, result.startTime()),
                () -> assertEquals(endTime, result.endTime())
        );
        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void createVisit_DoctorNotFound_ThrowsException() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endTime = startTime.plusHours(1);
        CreateVisitCommand command = new CreateVisitCommand(1L, startTime, endTime);
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class,
                () -> visitService.createVisit(command));
        // then
        Assertions.assertAll(
                () -> assertEquals("Doctor with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void createVisit_DoctorHasOverlappingVisit_ThrowsException() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endTime = startTime.plusHours(1);
        CreateVisitCommand command = new CreateVisitCommand(1L, startTime, endTime);
        Doctor doctor = new Doctor("Cardiologist");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(visitRepository.existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        1L, endTime, startTime)).thenReturn(true);
        // when
        DoctorOverlappingVisitException exception = assertThrows(DoctorOverlappingVisitException.class,
                () -> visitService.createVisit(command));
        // then
        Assertions.assertAll(
                () -> assertEquals("Doctor with id 1 has overlapping visit dates.", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void assignPatient_VisitAndPatientExist_PatientAssigned() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endTime = startTime.plusHours(1);
        Doctor doctor = new Doctor("Cardiologist");
        Visit visit = new Visit(startTime, endTime, doctor);
        Patient patient = new Patient("ABC123", "333444555", LocalDate.of(1990, 1, 1));
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(visitRepository.existsByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
                1L, endTime, startTime)).thenReturn(false);
        // when
        VisitDto result = visitService.assignPatient(1L, 1L);
        // then
        Assertions.assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(patient, visit.getPatient())
        );
    }

    @Test
    void assignPatient_VisitNotFound_ThrowsException() {
        // given
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        VisitNotFoundException exception = assertThrows(VisitNotFoundException.class,
                () -> visitService.assignPatient(1L, 1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void assignPatient_PatientNotFound_ThrowsException() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        Visit visit = new Visit(startTime, startTime.plusHours(1), new Doctor("Cardiologist")
        );
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> visitService.assignPatient(1L, 1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Patient with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void assignPatient_PatientHasOverlappingVisit_ThrowsException() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        LocalDateTime endTime = startTime.plusHours(1);
        Visit visit = new Visit(startTime, endTime, new Doctor("Cardiologist"));
        Patient patient = new Patient("ABC123", "333444555",
                LocalDate.of(1990, 1, 1));
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(visitRepository.existsByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
                1L, endTime, startTime)).thenReturn(true);
        // when
        PatientOverlappingVisitException exception = assertThrows(PatientOverlappingVisitException.class,
                () -> visitService.assignPatient(1L, 1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Patient with id 1 has another visit at this time", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void cancelVisit_VisitHasPatient_VisitCancelled() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        Doctor doctor = new Doctor("Cardiologist");
        Visit visit = new Visit(startTime, startTime.plusHours(1), doctor);
        Patient patient = new Patient("ABC123", "333444555",
                LocalDate.of(1990, 1, 1));
        visit.assignPatient(patient);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        // when
        visitService.cancelVisit(1L);
        // then
        Assertions.assertAll(
                () -> assertTrue(visit.isAvailable()),
                () -> assertNull(visit.getPatient())
        );
    }

    @Test
    void cancelVisit_VisitNotFound_ThrowsException() {
        // given
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        VisitNotFoundException exception = assertThrows(VisitNotFoundException.class,
                () -> visitService.cancelVisit(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void cancelVisit_VisitHasNoPatient_ThrowsException() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        Visit visit = new Visit(startTime, startTime.plusHours(1), new Doctor("Cardiologist"));
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        // when
        InvalidVisitDataException exception = assertThrows(InvalidVisitDataException.class,
                () -> visitService.cancelVisit(1L));
        // then
        assertEquals("Visit has no patient", exception.getMessage());
    }

    @Test
    void deleteVisit_AvailableVisitExists_VisitDeleted() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        Visit visit = new Visit(startTime, startTime.plusHours(1), new Doctor("Cardiologist"));
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        // when
        visitService.deleteVisit(1L);
        // then
        verify(visitRepository).delete(visit);
    }

    @Test
    void deleteVisit_VisitNotFound_ThrowsException() {
        // given
        when(visitRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        VisitNotFoundException exception = assertThrows(VisitNotFoundException.class,
                () -> visitService.deleteVisit(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Visit with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void deleteVisit_VisitAlreadyBooked_ThrowsException() {
        // given
        LocalDateTime startTime = LocalDateTime.now()
                .plusDays(7)
                .withHour(10)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);
        Visit visit = new Visit(startTime, startTime.plusHours(1), new Doctor("Cardiologist"));
        Patient patient = new Patient("ABC123", "333444555",
                LocalDate.of(1990, 1, 1));
        visit.assignPatient(patient);
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));
        // when
        VisitAlreadyBookedException exception = assertThrows(VisitAlreadyBookedException.class,
                () -> visitService.deleteVisit(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("Visit with id 1 already booked", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }
}