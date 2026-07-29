package com.andrzej_kosmowski.medical_clinic.dto;

public record UpdateFacilityCommand(
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber
) {
}
