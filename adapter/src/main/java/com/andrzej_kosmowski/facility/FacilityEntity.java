package com.andrzej_kosmowski.facility;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "facilities")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FacilityEntity {
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
//    @ManyToMany(mappedBy = "facilities")
//    private Set<Doctor> doctors = new HashSet<>();
//
//    public void assignDoctor(Doctor doctor) {
//        if (doctors.add(doctor)) {
//            doctor.getFacilities().add(this);
//        }
//    }
//
//    public void removeDoctor(Doctor doctor) {
//        if (doctors.remove(doctor)) {
//            doctor.getFacilities().remove(this);
//        }
//    }

    public FacilityEntity(String name, String city, String zipCode, String street, String buildingNumber) {
        this.name = name;
        this.city = city;
        this.zipCode = zipCode;
        this.street = street;
        this.buildingNumber = buildingNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FacilityEntity facility)) return false;
        return id != null && id.equals(facility.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
