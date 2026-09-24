package com.andrzej_kosmowski.medical_clinic.repository;

import com.andrzej_kosmowski.medical_clinic.dto.visit.VisitSearchCriteria;
import com.andrzej_kosmowski.medical_clinic.model.Visit;
import com.andrzej_kosmowski.medical_clinic.specification.VisitSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {
    List<Visit> findAllByDoctorIdAndPatientIsNullAndStartTimeAfter(Long doctorId, LocalDateTime startTime);
    List<Visit> findAllByDoctorId(Long doctorId);
    boolean existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long id, LocalDateTime end, LocalDateTime start
    );
    boolean existsByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long id, LocalDateTime end, LocalDateTime start
    );
    default List<Visit> search(VisitSearchCriteria criteria) {
        Specification<Visit> spec = VisitSpecifications.hasSpecialization(criteria.specialization())
                .and(VisitSpecifications.startsAfter(criteria.from()))
                .and(VisitSpecifications.startsBefore(criteria.to()))
                .and(VisitSpecifications.availableOnly(criteria.availableOnly()));
        return findAll(spec);
    }
}
