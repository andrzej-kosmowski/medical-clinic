package com.andrzej_kosmowski.medical_clinic.repository;

import com.andrzej_kosmowski.medical_clinic.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUserEmail(String email);
    boolean existsByIdCardNo(String idCardNo);
}
