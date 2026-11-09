package com.andrzej_kosmowski.facility;

public record UpdateFacilityCommand(
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber
) {
}
