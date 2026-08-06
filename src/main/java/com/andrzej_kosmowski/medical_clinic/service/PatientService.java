package com.andrzej_kosmowski.medical_clinic.service;

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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserService userService;
    private final PatientMapper patientMapper;
    private final UserMapper userMapper;

    public Page<PatientDto> getAllPatients(Pageable pageable) {
        return patientRepository.findAll(pageable)
                .map(patientMapper::toDto);
    }

    public PatientDto getPatientById(Long id) {
        Patient patient = findPatientOrThrow(id);
        return patientMapper.toDto(patient);
    }

    @Transactional
    public PatientDto addPatient(CreatePatientCommand command) {
        if (patientRepository.existsByIdCardNo(command.idCardNo())) {
            throw new PatientAlreadyExistsException(command.idCardNo());
        }
        Patient patient = patientMapper.from(command);
        patient.validate();
        User user = userService.createUser(userMapper.toUserCommand(command));
        patient.assignUser(user);
        Patient saved = patientRepository.save(patient);
        return patientMapper.toDto(saved);
    }

    @Transactional
    public void deletePatient(Long id) {
        Patient patient = findPatientOrThrow(id);
        patientRepository.delete(patient);
    }

    @Transactional
    public PatientDto updatePatient(Long id, UpdatePatientCommand command) {
        Patient patient = findPatientOrThrow(id);
        userService.validateEmailChange(patient.getUser(), command.email());
        patient.update(command);
        patient.getUser().update(command.firstName(), command.lastName(), command.email()
        );
        return patientMapper.toDto(patient);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordCommand command) {
        Patient patient = findPatientOrThrow(id);
        patient.getUser().changePassword(command.password());
    }

    private Patient findPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }
}
