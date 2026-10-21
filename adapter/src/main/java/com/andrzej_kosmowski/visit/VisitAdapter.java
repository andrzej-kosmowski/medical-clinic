package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.doctor.DoctorEntity;
import com.andrzej_kosmowski.doctor.DoctorJpaRepository;
import com.andrzej_kosmowski.doctor.DoctorNotFoundException;
import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import com.andrzej_kosmowski.patient.PatientEntity;
import com.andrzej_kosmowski.patient.PatientJpaRepository;
import com.andrzej_kosmowski.patient.PatientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class VisitAdapter implements VisitRepository {
    private final VisitJpaRepository visitJpaRepository;
    private final DoctorJpaRepository doctorJpaRepository;
    private final PatientJpaRepository patientJpaRepository;
    private final VisitMapper visitMapper;

    @Override
    public Optional<Visit> findById(long id) {
        return visitJpaRepository.findById(id).map(visitMapper::toDomain);
    }

    @Override
    public PageResult<Visit> findAll(PageQuery query) {
        Page<VisitEntity> page = visitJpaRepository.findAll(
                PageRequest.of(query.page(), query.size())
        );

        return new PageResult<>(
                page.getContent().stream()
                        .map(visitMapper::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public List<Visit> findByPatientId(long patientId) {
        return visitJpaRepository.findAllByPatientId(patientId).stream()
                .map(visitMapper::toDomain)
                .toList();
    }

    @Override
    public List<Visit> findByDoctorId(long doctorId) {
        return visitJpaRepository.findAllByDoctorId(doctorId).stream()
                .map(visitMapper::toDomain)
                .toList();
    }

    @Override
    public List<Visit> findAvailableByDoctorId(long doctorId, LocalDateTime after) {
        return visitJpaRepository.findAllByDoctorIdAndPatientIsNullAndStartTimeAfter(doctorId, after)
                .stream()
                .map(visitMapper::toDomain)
                .toList();
    }

    @Override
    public List<Visit> search(VisitSearchCriteria query) {
        return visitJpaRepository.search(query).stream()
                .map(visitMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsDoctorOverlap(long doctorId, LocalDateTime startTime, LocalDateTime endTime) {
        return visitJpaRepository.existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
                doctorId, endTime, startTime
        );
    }

    @Override
    public boolean existsPatientOverlap(long patientId, LocalDateTime startTime, LocalDateTime endTime) {
        return visitJpaRepository.existsByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
                patientId, endTime, startTime
        );
    }

    @Transactional
    @Override
    public Visit save(Visit visit) {
        VisitEntity entity = visitMapper.toEntity(visit);
        DoctorEntity doctor = doctorJpaRepository.findById(visit.getDoctorId())
                .orElseThrow(() -> new DoctorNotFoundException(visit.getDoctorId()));
        entity.setDoctor(doctor);
        if (visit.getPatientId() != null) {
            PatientEntity patient = patientJpaRepository.findById(visit.getPatientId())
                    .orElseThrow(() -> new PatientNotFoundException(visit.getPatientId()));
            entity.setPatient(patient);
        }
        return visitMapper.toDomain(visitJpaRepository.save(entity));
    }

    @Transactional
    @Override
    public void delete(Visit visit) {
        visitJpaRepository.deleteById(visit.getId());
    }
}
