package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.patient.CreatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.patient.PatientDto;
import com.andrzej_kosmowski.medical_clinic.dto.patient.UpdatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.ChangePasswordCommand;
import com.andrzej_kosmowski.medical_clinic.exception.patient.PatientAlreadyExistsException;
import com.andrzej_kosmowski.medical_clinic.exception.patient.PatientNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.PatientMapper;
import com.andrzej_kosmowski.medical_clinic.mapper.UserMapper;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import com.andrzej_kosmowski.medical_clinic.model.User;
import com.andrzej_kosmowski.medical_clinic.repository.PatientRepository;
import com.andrzej_kosmowski.medical_clinic.repository.UserRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatientServiceTest {
    PatientService patientService;
    UserService userService;
    PatientRepository patientRepository;
    UserRepository userRepository;
    PatientMapper patientMapper;
    UserMapper userMapper;

    @BeforeEach
    void setUp() {
        this.patientRepository = Mockito.mock(PatientRepository.class);
        this.patientMapper = Mappers.getMapper(PatientMapper.class);
        this.userRepository = Mockito.mock(UserRepository.class);
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.userService = new UserService(userRepository, userMapper);
        this.patientService = new PatientService(patientRepository, userService, patientMapper, userMapper);
    }

    @Test
    void getAllPatients_PatientsExist_PatientsReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Patient> patients = new ArrayList<>();
        patients.add(new Patient("ABC123", "333444555", LocalDate.of(1980, 1, 1)));
        patients.add(new Patient("ZXC321", "666777888", LocalDate.of(1999, 2, 5)));
        Page<Patient> page = new PageImpl<>(patients);
        when(patientRepository.findAll(pageable)).thenReturn(page);
        // when
        PageResponse<PatientDto> result = patientService.getAllPatients(pageable);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("ABC123", result.content().get(0).idCardNo()),
                () -> assertEquals("ZXC321", result.content().get(1).idCardNo()),
                () -> assertEquals("333444555", result.content().get(0).phoneNumber()),
                () -> assertEquals("666777888", result.content().get(1).phoneNumber()),
                () -> assertEquals(LocalDate.of(1980, 1, 1), result.content().get(0).birthday()),
                () -> assertEquals(LocalDate.of(1999, 2, 5), result.content().get(1).birthday())
        );
    }

    @Test
    void getPatientById_PatientExists_PatientReturned() {
        // given
        Patient patient = new Patient("ABC123", "333444555", LocalDate.of(1980, 1, 1));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        // when
        PatientDto result = patientService.getPatientById(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals("ABC123", result.idCardNo()),
                () -> assertEquals("333444555", result.phoneNumber()),
                () -> assertEquals(LocalDate.of(1980, 1, 1), result.birthday())
        );
    }

    @Test
    void addPatient_ValidPatient_PatientAdded() {
        // given
        CreatePatientCommand command = new CreatePatientCommand("test@test.pl", "pass123",
                "ABC123", "Jan", "Nowak", "111222333",
                LocalDate.of(1980, 1, 1));
        when(patientRepository.existsByIdCardNo("ABC123")).thenReturn(false);
        when(userRepository.existsByEmail("test@test.pl")).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(patientRepository.save(any(Patient.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        // when
        PatientDto result = patientService.addPatient(command);
        // then
        Assertions.assertAll(
                () -> assertEquals("ABC123", result.idCardNo()),
                () -> assertEquals("test@test.pl", result.email()),
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Nowak", result.lastName()),
                () -> assertEquals("111222333", result.phoneNumber()),
                () -> assertEquals(LocalDate.of(1980, 1, 1), result.birthday())
        );
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    void updatePatient_ValidCommand_PatientUpdated() {
        // given
        Patient patient = new Patient("ABC123", "333444555", LocalDate.of(1980, 1, 1));
        User user = new User("Jan", "Nowak", "test@test.pl", "pass123");
        patient.assignUser(user);
        UpdatePatientCommand command = new UpdatePatientCommand("newtest@test.pl", "ZXC321",
                "Adam", "Nowak", "333222111", LocalDate.of(1995, 5, 5));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        when(userRepository.existsByEmail("newtest@test.pl")).thenReturn(false);
        // when
        PatientDto result = patientService.updatePatient(1L, command);
        // then
        Assertions.assertAll(
                () -> assertEquals("ZXC321", result.idCardNo()),
                () -> assertEquals("333222111", result.phoneNumber()),
                () -> assertEquals(LocalDate.of(1995, 5, 5), result.birthday()),
                () -> assertEquals("newtest@test.pl", result.email()),
                () -> assertEquals("Adam", result.firstName()),
                () -> assertEquals("Nowak", result.lastName())
        );
    }

    @Test
    void deletePatient_PatientExists_PatientDeleted() {
        // given
        Patient patient = new Patient("ABC123", "333444555", LocalDate.of(1980, 1, 1));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        // when
        patientService.deletePatient(1L);
        // then
        verify(patientRepository).delete(patient);
    }

    @Test
    void changePassword_PatientExists_PasswordChanged() {
        // given
        Patient patient = new Patient("ABC123", "333444555", LocalDate.of(1980, 1, 1));
        User user = new User("Jan", "Nowak", "test@test.pl", "pass1");
        patient.assignUser(user);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));
        ChangePasswordCommand command = new ChangePasswordCommand("newpass123");
        // when
        patientService.changePassword(1L, command);
        // then
        assertEquals("newpass123", patient.getUser().getPassword());
    }

    @Test
    void getPatientById_PatientDoesNotExist_ThrowsException() {
        // given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> patientService.getPatientById(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("Patient with id 1 not found", exception.getMessage())
        );

    }

    @Test
    void addPatient_PatientAlreadyExists_ThrowsException() {
        // given
        CreatePatientCommand command = new CreatePatientCommand("test@test.pl", "pass123",
                "ABC123", "Jan", "Nowak", "111222333",
                LocalDate.of(1980, 1, 1));
        when(patientRepository.existsByIdCardNo("ABC123")).thenReturn(true);
        // when
        PatientAlreadyExistsException exception = assertThrows(PatientAlreadyExistsException.class,
                () -> patientService.addPatient(command));
        // then
        Assertions.assertAll(
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus()),
                () -> assertEquals("Patient with id card number ABC123 already exists", exception.getMessage())
        );
    }

    @Test
    void updatePatient_PatientDoesNotExist_ThrowsException() {
        // given
        UpdatePatientCommand command = new UpdatePatientCommand("newtest@test.pl", "ZXC321",
                "Adam", "Nowak", "333222111", LocalDate.of(1995, 5, 5));
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> patientService.updatePatient(1L, command));
        // then
        Assertions.assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("Patient with id 1 not found", exception.getMessage())
        );
    }

    @Test
    void deletePatient_PatientDoesNotExist_ThrowsException() {
        // given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> patientService.deletePatient(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("Patient with id 1 not found", exception.getMessage())
        );
    }

    @Test
    void changePassword_PatientDoesNotExist_ThrowsException() {
        // given
        when(patientRepository.findById(1L)).thenReturn(Optional.empty());
        ChangePasswordCommand command = new ChangePasswordCommand("newpass123");
        // when
        PatientNotFoundException exception = assertThrows(PatientNotFoundException.class,
                () -> patientService.changePassword(1L, command));
        // then
        Assertions.assertAll(
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus()),
                () -> assertEquals("Patient with id 1 not found", exception.getMessage())
        );
    }
}