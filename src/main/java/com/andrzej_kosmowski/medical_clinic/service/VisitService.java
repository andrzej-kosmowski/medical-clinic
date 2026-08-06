package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.visit.CreateVisitCommand;
import com.andrzej_kosmowski.medical_clinic.dto.visit.VisitDto;
import com.andrzej_kosmowski.medical_clinic.exception.doctor.DoctorNotFoundException;
import com.andrzej_kosmowski.medical_clinic.exception.patient.PatientNotFoundException;
import com.andrzej_kosmowski.medical_clinic.exception.visit.DoctorOverlappingVisitException;
import com.andrzej_kosmowski.medical_clinic.exception.visit.PatientOverlappingVisitException;
import com.andrzej_kosmowski.medical_clinic.exception.visit.VisitAlreadyBookedException;
import com.andrzej_kosmowski.medical_clinic.exception.visit.VisitNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.VisitMapper;
import com.andrzej_kosmowski.medical_clinic.model.Doctor;
import com.andrzej_kosmowski.medical_clinic.model.Patient;
import com.andrzej_kosmowski.medical_clinic.model.Visit;
import com.andrzej_kosmowski.medical_clinic.repository.DoctorRepository;
import com.andrzej_kosmowski.medical_clinic.repository.PatientRepository;
import com.andrzej_kosmowski.medical_clinic.repository.VisitRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitMapper visitMapper;

    public Page<VisitDto> getAllVisits(Pageable pageable) {
        return visitRepository.findAll(pageable)
                .map(visitMapper::toDto);
    }

    public VisitDto getVisitById(long id) {
        Visit visit = findVisitOrThrow(id);
        return visitMapper.toDto(visit);
    }

    public List<VisitDto> getAvailableVisits() {
        return visitRepository.findAllByPatientIsNullAndStartTimeAfter(LocalDateTime.now())
                .stream()
                .map(visitMapper::toDto)
                .toList();
    }

    public List<VisitDto> getPatientVisits(long patientId) {
        Patient patient = findPatientOrThrow(patientId);
        return patient.getVisits().stream()
                .map(visitMapper::toDto)
                .toList();
    }

    public List<VisitDto> getDoctorVisits(long doctorId) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        return doctor.getVisits().stream()
                .map(visitMapper::toDto)
                .toList();
    }

    @Transactional
    public VisitDto createVisit(CreateVisitCommand command) {
        Doctor doctor = findDoctorOrThrow(command.doctorId());
        Visit visit = visitMapper.from(command);
        visit.assignDoctor(doctor);
        validateDoctorAvailability(command.doctorId(), command.startTime(), command.endTime());
        visit.validate();
        Visit saved = visitRepository.save(visit);
        return visitMapper.toDto(saved);
    }

    @Transactional
    public VisitDto assignPatient(Long visitId, Long patientId) {
        Visit visit = findVisitOrThrow(visitId);
        Patient patient = findPatientOrThrow(patientId);
        validatePatientAvailability(patientId, visit.getStartTime(), visit.getEndTime());
        visit.assignPatient(patient);
        return visitMapper.toDto(visit);
    }

    @Transactional
    public void cancelVisit(long visitId) {
        Visit visit = findVisitOrThrow(visitId);
        visit.cancelVisit();
    }

    @Transactional
    public void deleteVisit(long visitId) {
        Visit visit = findVisitOrThrow(visitId);
        if (!visit.isAvailable()) {
            throw new VisitAlreadyBookedException(visitId);
        }
        visitRepository.delete(visit);
    }

    private void validateDoctorAvailability(Long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        boolean overlaps = visitRepository.existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
                doctorId, endTime, startTime);
        if (overlaps) {
            throw new DoctorOverlappingVisitException(doctorId);
        }
    }

    private void validatePatientAvailability(Long patientId, LocalDateTime startTime, LocalDateTime endTime) {
        boolean overlaps = visitRepository.existsByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
                patientId, endTime, startTime);
        if (overlaps) {
            throw new PatientOverlappingVisitException(patientId);
        }
    }

    private Doctor findDoctorOrThrow(long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }

    private Patient findPatientOrThrow(long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }
    private Visit findVisitOrThrow(Long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException(id));
    }
}
