package com.andrzej_kosmowski.facility;

public record FacilityDto(
        Long id,
        String name,
        String city,
        String zipCode,
        String street,
        String buildingNumber
) {
}
