package com.andrzej_kosmowski.doctor;


import com.andrzej_kosmowski.facility.Facility;
import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;

import java.util.List;

public interface DoctorUseCase {
    Doctor getById(long id);
    PageResult<Doctor> findAll(PageQuery query);
    List<Doctor> getBySpecialization(String specialization);
    List<Doctor> getByFacilityId(Long facilityId);
    Doctor create(CreateDoctorCommand command);
    Doctor update(long id, UpdateDoctorCommand command);
    Doctor assignFacility(long doctorId, long facilityId);
    List<Facility> getFacilities(long doctorId);
    void removeFacility(long doctorId, long facilityId);
    void delete(long id);
}
