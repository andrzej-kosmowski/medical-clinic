package com.andrzej_kosmowski.patient;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientJpaRepository extends JpaRepository<PatientEntity, Long> {
    boolean existsByIdCardNo(String idCardNo);
    boolean existsByIdCardNoAndIdNot(String idCardNo, long id);
}
