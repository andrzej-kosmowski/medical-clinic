package com.andrzej_kosmowski.medical_clinic.dto.facility;

public record CreateFacilityCommand(
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber
) {
}
