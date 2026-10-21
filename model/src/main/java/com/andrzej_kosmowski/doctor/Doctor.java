package com.andrzej_kosmowski.doctor;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Doctor {
    private Long id;
    private String specialization;
    private Long userId;
    private Set<Long> facilityIds = new HashSet<>();

    public Doctor(Long id, String specialization, Long userId, Set<Long> facilityIds) {
        this.id = id;
        this.specialization = specialization;
        this.userId = userId;
        if (facilityIds != null) {
            this.facilityIds.addAll(facilityIds);
        }
        this.validate();
    }

    public void updateSpecialization(String specialization) {
        this.specialization = specialization;
        this.validate();
    }

    public void validate() {
        if (userId == null || userId < 1) {
            throw new InvalidDoctorDataException("User id must be provided");
        }
        if (specialization == null || specialization.isBlank()) {
            throw new InvalidDoctorDataException("Specialization cannot be empty");
        }
        facilityIds.forEach(this::validateFacilityId);
    }

    public void assignFacility(Long facilityId) {
        validateFacilityId(facilityId);
        facilityIds.add(facilityId);
    }

    public void removeFacility(Long facilityId) {
        validateFacilityId(facilityId);
        facilityIds.remove(facilityId);
    }

    private void validateFacilityId(Long facilityId) {
        if (facilityId == null || facilityId < 1) {
            throw new InvalidDoctorDataException("Facility id must be positive");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Doctor doctor)) return false;
        return id != null && id.equals(doctor.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
