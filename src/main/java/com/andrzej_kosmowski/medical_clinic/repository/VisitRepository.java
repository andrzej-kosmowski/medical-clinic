package com.andrzej_kosmowski.medical_clinic.repository;

import com.andrzej_kosmowski.medical_clinic.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByPatientIsNullAndStartTimeAfter(LocalDateTime startTime);
    List<Visit> findAllByDoctorIdAndPatientIsNullAndStartTimeAfter(Long doctorId, LocalDateTime startTime);
    List<Visit> findAllByDoctorSpecializationIgnoreCaseAndPatientIsNullAndStartTimeGreaterThanEqualAndStartTimeLessThan(
            String specialization, LocalDateTime startTime, LocalDateTime endTime);
    List<Visit> findAllByDoctorId(Long doctorId);
    List<Visit> findAllByDoctorSpecializationIgnoreCaseAndStartTimeGreaterThanEqualAndStartTimeLessThan(
            String specialization, LocalDateTime startTime, LocalDateTime endTime);
    @Query("""
        SELECT v
        FROM Visit v
        JOIN v.doctor d
        WHERE v.patient is NULL
            AND v.startTime >= :from
            AND v.startTime < :to
            AND (:specialization is NULL OR LOWER(d.specialization) = LOWER(:specialization))
        """)
    List<Visit> findAvailableInRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("specialization") String specialization);
    boolean existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long id, LocalDateTime end, LocalDateTime start
    );
    boolean existsByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long id, LocalDateTime end, LocalDateTime start
    );
}
