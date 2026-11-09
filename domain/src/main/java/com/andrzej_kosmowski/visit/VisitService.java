package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.doctor.DoctorUseCase;
import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import com.andrzej_kosmowski.patient.PatientUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class VisitService implements VisitUseCase {
    private final VisitRepository visitRepository;
    private final DoctorUseCase doctorUseCase;
    private final PatientUseCase patientUseCase;

    @Override
    public Visit getById(long id) {
        return findVisitOrThrow(id);
    }

    @Override
    public PageResult<Visit> getAll(PageQuery query) {
        if (query.page() < 0 || query.size() < 1) {
            throw new IllegalArgumentException("Invalid page or size");
        }
        return visitRepository.findAll(query);
    }

    @Override
    public List<Visit> getPatientVisits(long patientId) {
        patientUseCase.getById(patientId);
        return visitRepository.findByPatientId(patientId);
    }

    @Override
    public List<Visit> getDoctorVisits(long doctorId) {
        doctorUseCase.getById(doctorId);
        return visitRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<Visit> getAvailableDoctorVisits(long doctorId) {
        doctorUseCase.getById(doctorId);
        return visitRepository.findAvailableByDoctorId(doctorId, LocalDateTime.now());
    }

    @Override
    public List<Visit> search(VisitSearchCriteria query) {
        LocalDateTime from = query.effectiveFrom();
        LocalDateTime to = query.effectiveTo();

        if (from != null && to != null && !to.isAfter(from)) {
            throw new InvalidVisitDataException("Range end must be after range start");
        }
        return visitRepository.search(query);
    }

    @Override
    public Visit create(CreateVisitCommand command) {
        if (command.doctorId() == null || command.doctorId() < 1) {
            throw new InvalidVisitDataException("Invalid doctor id");
        }
        doctorUseCase.getById(command.doctorId());
        Visit visit = new Visit(null, command.startTime(), command.endTime(), command.doctorId(), null);
        visit.validate();
        if (visitRepository.existsDoctorOverlap(command.doctorId(), command.startTime(), command.endTime())) {
            throw new DoctorOverlappingVisitException(command.doctorId());
        }
        return visitRepository.save(visit);
    }

    @Override
    public Visit assignPatient(long visitId, long patientId) {
        Visit visit = findVisitOrThrow(visitId);
        patientUseCase.getById(patientId);
        if (visitRepository.existsPatientOverlap(patientId, visit.getStartTime(), visit.getEndTime())) {
            throw new PatientOverlappingVisitException(patientId);
        }
        visit.assignPatient(patientId);
        return visitRepository.save(visit);
    }

    @Override
    public void cancel(long visitId) {
        Visit visit = findVisitOrThrow(visitId);
        visit.cancelVisit();
        visitRepository.save(visit);
    }

    @Override
    public void delete(long visitId) {
        Visit visit = findVisitOrThrow(visitId);
        if (!visit.isAvailable()) {
            visit.cancelVisit();
        }
        visitRepository.delete(visit);
    }

    private Visit findVisitOrThrow(long id) {
        return visitRepository.findById(id)
                .orElseThrow(() -> new VisitNotFoundException(id));
    }
}
