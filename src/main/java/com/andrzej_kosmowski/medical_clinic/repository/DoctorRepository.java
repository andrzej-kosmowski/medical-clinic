package com.andrzej_kosmowski.medical_clinic.repository;

import com.andrzej_kosmowski.medical_clinic.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> { }
