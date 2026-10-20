package com.andrzej_kosmowski.facility;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Facility {
    private Long id;
    private String name;
    private String city;
    private String zipCode;
    private String street;
    private String buildingNumber;

    public Facility(Long id, String name, String city, String zipCode, String street, String buildingNumber) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.buildingNumber = buildingNumber;
    }

    public void update(String name, String city, String zipCode, String street, String buildingNumber) {
        this.name = name;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.buildingNumber = buildingNumber;
        this.validate();
    }

    public void validate() {
        if (name == null || name.isBlank()) {
            throw new InvalidFacilityDataException("Name cannot be empty");
        }
        if (city == null || city.isBlank()) {
            throw new InvalidFacilityDataException("City cannot be empty");
        }
        if (zipCode == null || !zipCode.matches("^\\d{2}-\\d{3}$")) {
            throw new InvalidFacilityDataException("Zip code must match format XX-XXX");
        }
        if (street == null || street.isBlank()) {
            throw new InvalidFacilityDataException("Street cannot be empty");
        }
        if (buildingNumber == null || buildingNumber.isBlank()) {
            throw new InvalidFacilityDataException("Building number cannot be empty");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Facility facility)) return false;
        return id != null && id.equals(facility.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
