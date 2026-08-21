package com.andrzej_kosmowski.medical_clinic.model;

import com.andrzej_kosmowski.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.exception.facility.InvalidFacilityDataException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "facilities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Facility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String zipCode;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String buildingNumber;
    @ManyToMany(mappedBy = "facilities")
    private Set<Doctor> doctors = new HashSet<>();

    public void assignDoctor(Doctor doctor) {
        if (doctors.add(doctor)) {
            doctor.getFacilities().add(this);
        }
    }

    public void removeDoctor(Doctor doctor) {
        if (doctors.remove(doctor)) {
            doctor.getFacilities().remove(this);
        }
    }

    public Facility(String name, String city, String zipCode, String street, String buildingNumber) {
        this.name = name;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.buildingNumber = buildingNumber;
    }

    public void update(UpdateFacilityCommand command) {
        this.name = command.name();
        this.city = command.city();
        this.zipCode = command.zipCode();
        this.street = command.street();
        this.buildingNumber = command.buildingNumber();
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
