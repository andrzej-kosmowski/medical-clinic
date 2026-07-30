package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.patient.CreatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.patient.PatientDto;
import com.andrzej_kosmowski.medical_clinic.dto.patient.UpdatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.ChangePasswordCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.CreateUserCommand;
import com.andrzej_kosmowski.medical_clinic.exception.patient.PatientAlreadyExistsException;
import com.andrzej_kosmowski.medical_clinic.exception.patient.PatientNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.PatientMapper;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import com.andrzej_kosmowski.medical_clinic.model.User;
import com.andrzej_kosmowski.medical_clinic.repository.PatientRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserService userService;
    private final PatientMapper patientMapper;

    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDto)
                .toList();
    }

    public PatientDto getPatientByEmail(String email) {
        Patient patient = findPatientOrThrow(email);
        return patientMapper.toDto(patient);
    }

    @Transactional
    public PatientDto addPatient(CreatePatientCommand command) {
        if (patientRepository.existsByIdCardNo(command.idCardNo())) {
            throw new PatientAlreadyExistsException(command.idCardNo());
        }
        Patient patient = patientMapper.from(command);
        patient.validate();
        CreateUserCommand userCommand = new CreateUserCommand(
                command.firstName(),
                command.lastName(),
                command.email(),
                command.password()
        );
        User user = userService.createUser(userCommand);
        patient.assignUser(user);
        Patient saved = patientRepository.save(patient);
        return patientMapper.toDto(saved);
    }

    @Transactional
    public void deletePatientByEmail(String email) {
        Patient patient = findPatientOrThrow(email);
        patientRepository.delete(patient);
    }

    @Transactional
    public PatientDto updatePatientByEmail(String email, UpdatePatientCommand command) {
        Patient patient = findPatientOrThrow(email);
        userService.validateEmailChange(
                patient.getUser(),
                command.email());
        patient.update(command);
        patient.getUser().update(
                command.firstName(),
                command.lastName(),
                command.email()
        );
        return patientMapper.toDto(patient);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordCommand command) {
        Patient patient = findPatientOrThrow(email);
        patient.getUser().changePassword(command.password());
    }

    private Patient findPatientOrThrow(String email) {
        return patientRepository.findByUserEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }
}
