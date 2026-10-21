package com.andrzej_kosmowski.visit;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitJpaRepository extends JpaRepository<VisitEntity, Long>, JpaSpecificationExecutor<VisitEntity> {
    List<VisitEntity> findAllByDoctorId(Long doctorId);
    List<VisitEntity> findAllByPatientId(Long patientId);
    List<VisitEntity> findAllByDoctorIdAndPatientIsNullAndStartTimeAfter(
            Long doctorId, LocalDateTime start);
    boolean existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long doctorId, LocalDateTime end, LocalDateTime start);
    boolean existsByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long patientId, LocalDateTime end, LocalDateTime start
    );
    default List<VisitEntity> search(VisitSearchCriteria criteria) {
        Specification<VisitEntity> spec = VisitSpecifications.hasSpecialization(criteria.specialization())
                .and(VisitSpecifications.startsAfter(criteria.from()))
                .and(VisitSpecifications.startsBefore(criteria.to()))
                .and(VisitSpecifications.availableOnly(criteria.availableOnly()));
        return findAll(spec);
    }
}
