package com.andrzej_kosmowski.patient;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;

import java.util.Optional;

public interface PatientRepository {
    Optional<Patient> findById(long id);
    PageResult<Patient> findAll(PageQuery query);
    boolean existsByIdCardNo(String idCardNo);
    boolean existsByIdCardNoAndIdNot(String idCardNo, long id);
    Patient save(Patient patient);
    void delete(Patient patient);
}
