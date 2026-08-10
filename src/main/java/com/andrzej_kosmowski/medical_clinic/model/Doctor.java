package com.andrzej_kosmowski.medical_clinic.model;

import com.andrzej_kosmowski.medical_clinic.exception.doctor.InvalidDoctorDataException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "doctors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String specialization;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToMany
    @JoinTable(
            name = "doctor_facilities",
            joinColumns = @JoinColumn(name = "doctor_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> facilities = new HashSet<>();
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Visit> visits = new ArrayList<>();

    public Doctor(String specialization) {
        this.specialization = specialization;
    }

    public static Doctor create(String specialization, User user) {
        Doctor doctor = new Doctor(specialization);
        doctor.assignUser(user);
        doctor.validate();
        return doctor;
    }

    public void updateSpecialization(String specialization) {
        this.specialization = specialization;
        this.validate();
    }

    public void validate() {
        if (specialization == null || specialization.isBlank()) {
            throw new InvalidDoctorDataException("Specialization cannot be empty");
        }
    }

    public void assignUser(User user) {
        this.user = user;
        user.assignDoctor(this);
    }

    public void assignFacility(Facility facility) {
        if (facilities.add(facility)) {
            facility.assignDoctor(this);
        }
    }

    public void addVisit(Visit visit) {
        if (!visits.contains(visit)) {
            visits.add(visit);
        }
    }

    public void removeFacility(Facility facility) {
        if (facilities.remove(facility)) {
            facility.removeDoctor(this);
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
