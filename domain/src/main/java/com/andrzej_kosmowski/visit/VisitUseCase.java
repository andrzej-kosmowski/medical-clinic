package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;

import java.util.List;

public interface VisitUseCase {
    Visit getById(long id);
    PageResult<Visit> getAll(PageQuery query);
    List<Visit> getPatientVisits(long patientId);
    List<Visit> getDoctorVisits(long doctorId);
    List<Visit> getAvailableDoctorVisits(long doctorId);
    List<Visit> search(VisitSearchCriteria query);
    Visit create(CreateVisitCommand command);
    Visit assignPatient(long visitId, long patientId);
    void cancel(long visitId);
    void delete(long visitId);
}
