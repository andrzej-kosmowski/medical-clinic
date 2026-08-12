package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository visitRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final VisitMapper visitMapper;

    public PageResponse<VisitDto> getAllVisits(Pageable pageable) {
        return PageResponse.from(visitRepository.findAll(pageable)
                .map(visitMapper::toDto));
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
        log.info("Creating visit for doctorId={}, startTime={}, endTime={}",
                command.doctorId(), command.startTime(), command.endTime());
        Doctor doctor = findDoctorOrThrow(command.doctorId());
        validateDoctorAvailability(command.doctorId(), command.startTime(), command.endTime());
        Visit visit = visitMapper.from(command);
        visit.assignDoctor(doctor);
        visit.validate();
        Visit saved = visitRepository.save(visit);
        log.info("Visit created successfully: visit id={}, doctorId={}, startTime={}, endTime={}",
                saved.getId(), command.doctorId(), command.startTime(), command.endTime());
        return visitMapper.toDto(saved);
    }

    @Transactional
    public VisitDto assignPatient(Long visitId, Long patientId) {
        log.info("Assigning patientId={} to visitId={}", patientId, visitId);
        Visit visit = findVisitOrThrow(visitId);
        Patient patient = findPatientOrThrow(patientId);
        validatePatientAvailability(patientId, visit.getStartTime(), visit.getEndTime());
        visit.assignPatient(patient);
        log.info("Patient assigned successfully: patientId={}, visitId={}", patientId, visitId);
        return visitMapper.toDto(visit);
    }

    @Transactional
    public void cancelVisit(long visitId) {
        log.info("Canceling visitId={}", visitId);
        Visit visit = findVisitOrThrow(visitId);
        visit.cancelVisit();
        log.info("Visit cancelled successfully: visitId={}", visitId);
    }

    @Transactional
    public void deleteVisit(long visitId) {
        log.info("Deleting visitId={}", visitId);
        Visit visit = findVisitOrThrow(visitId);
        if (!visit.isAvailable()) {
            throw new VisitAlreadyBookedException(visitId);
        }
        visitRepository.delete(visit);
        log.info("Visit deleted successfully: visitId={}", visitId);
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
