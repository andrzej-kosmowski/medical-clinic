package com.andrzej_kosmowski.doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorJpaRepository extends JpaRepository<DoctorEntity, Long> {
    List<DoctorEntity> findAllBySpecializationIgnoreCase(String specialization);
    List<DoctorEntity> findAllByFacilitiesId(Long facilitiesId);
}
