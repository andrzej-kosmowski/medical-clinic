package com.andrzej_kosmowski.medical_clinic.repository;

import com.andrzej_kosmowski.medical_clinic.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityRepository extends JpaRepository<Facility, Long> {
    boolean existsByName(String name);
}
