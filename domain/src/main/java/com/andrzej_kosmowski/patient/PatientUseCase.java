package com.andrzej_kosmowski.patient;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;

public interface PatientUseCase {
    Patient getById(long id);
    PageResult<Patient> getAll(PageQuery query);
    Patient create(CreatePatientCommand command);
    Patient update(long id, UpdatePatientCommand command);
    void changePassword(long id, String newPassword);
    void delete(long id);
}
