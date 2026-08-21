package com.andrzej_kosmowski.medical_clinic.repository;

import com.andrzej_kosmowski.medical_clinic.model.Visit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findAllByPatientIsNullAndStartTimeAfter(LocalDateTime startTime);
    boolean existsByDoctorIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long id, LocalDateTime end, LocalDateTime start
    );
    boolean existsByPatientIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long id, LocalDateTime end, LocalDateTime start
    );
}
