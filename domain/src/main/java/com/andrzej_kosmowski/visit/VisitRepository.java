package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitRepository {
    Optional<Visit> findById(long id);
    PageResult<Visit> findAll(PageQuery query);
    List<Visit> findByPatientId(long patientId);
    List<Visit> findByDoctorId(long doctorId);
    List<Visit> findAvailableByDoctorId(long doctorId, LocalDateTime after);
    List<Visit> search(VisitSearchCriteria query);
    boolean existsDoctorOverlap(long doctorId, LocalDateTime startTime, LocalDateTime endTime);
    boolean existsPatientOverlap(long patientId, LocalDateTime startTime, LocalDateTime endTime);
    Visit save(Visit visit);
    void delete(Visit visit);
}
