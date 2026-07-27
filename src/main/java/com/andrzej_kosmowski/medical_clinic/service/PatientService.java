package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.ChangePasswordCommand;
import com.andrzej_kosmowski.medical_clinic.dto.CreatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.PatientDto;
import com.andrzej_kosmowski.medical_clinic.dto.UpdatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.exception.PatientAlreadyExistsException;
import com.andrzej_kosmowski.medical_clinic.exception.PatientNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.PatientMapper;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import com.andrzej_kosmowski.medical_clinic.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
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

    public PatientDto addPatient(CreatePatientCommand command) {
        if (patientRepository.existsByEmail(command.email())) {
            throw new PatientAlreadyExistsException(command.email());
        }
        Patient patient = patientMapper.from(command);
        patient.validate();
        Patient saved = patientRepository.save(patient);
        return patientMapper.toDto(saved);
    }

    public void deletePatientByEmail(String email) {
        Patient patient = findPatientOrThrow(email);
        patientRepository.delete(patient);
    }

    public PatientDto updatePatientByEmail(String email, UpdatePatientCommand command) {
        Patient existing = findPatientOrThrow(email);
        existing.update(command);
        Patient updated = patientRepository.save(existing);
        return patientMapper.toDto(updated);
    }

    public void changePassword(String email, ChangePasswordCommand command) {
        Patient patient = findPatientOrThrow(email);
        patient.changePassword(command.password());
        patientRepository.save(patient);
    }

    private Patient findPatientOrThrow(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new PatientNotFoundException(email));
    }
}
