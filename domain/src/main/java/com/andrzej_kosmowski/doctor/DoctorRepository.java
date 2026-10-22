package com.andrzej_kosmowski.doctor;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository {
    Optional<Doctor> findById(long id);
    PageResult<Doctor> findAll(PageQuery query);
    List<Doctor> findBySpecialization(String specialization);
    List<Doctor> findByFacilityId(long facilitiesId);
    Doctor save(Doctor doctor);
    void delete(Doctor doctor);
}
