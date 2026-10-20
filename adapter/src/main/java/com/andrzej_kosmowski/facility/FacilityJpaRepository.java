package com.andrzej_kosmowski.facility;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FacilityJpaRepository extends JpaRepository<FacilityEntity, Long> {
    boolean existsByName(String name);
}
