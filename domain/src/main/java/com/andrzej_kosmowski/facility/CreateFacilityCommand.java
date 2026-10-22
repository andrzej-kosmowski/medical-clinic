package com.andrzej_kosmowski.facility;

public record CreateFacilityCommand(
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber
) {
}
