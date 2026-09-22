package com.andrzej_kosmowski.medical_clinic.repository;

import com.andrzej_kosmowski.medical_clinic.model.Visit;
import com.andrzej_kosmowski.medical_clinic.specification.VisitSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long>, JpaSpecificationExecutor<Visit> {
    List<Visit> findAllByPatientIsNullAndStartTimeAfter(LocalDateTime startTime);
    List<Visit> findAllByDoctorIdAndPatientIsNullAndStartTimeAfter(Long doctorId, LocalDateTime startTime);
    List<Visit> findAllByDoctorSpecializationIgnoreCaseAndPatientIsNullAndStartTimeGreaterThanEqualAndStartTimeLessThan(
            String specialization, LocalDateTime startTime, LocalDateTime endTime);
    List<Visit> findAllByDoctorId(Long doctorId);
    List<Visit> findAllByDoctorSpecializationIgnoreCaseAndStartTimeGreaterThanEqualAndStartTimeLessThan(
            String specialization, LocalDateTime startTime, LocalDateTime endTime);
    default List<Visit> findAvailableInRange(LocalDateTime from, LocalDateTime to, String specialization) {
        Specification<Visit> spec = VisitSpecifications.hasNoPatient()
                .and(VisitSpecifications.startsBetween(from, to))
                .and(VisitSpecifications.hasSpecialization(specialization));
        return findAll(spec);
    }
    boolean existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long id, LocalDateTime end, LocalDateTime start
    );
    boolean existsByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long id, LocalDateTime end, LocalDateTime start
    );
}
