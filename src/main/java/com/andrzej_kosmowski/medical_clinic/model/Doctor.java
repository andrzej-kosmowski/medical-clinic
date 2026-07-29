package com.andrzej_kosmowski.medical_clinic.model;

import com.andrzej_kosmowski.medical_clinic.exception.doctor.InvalidDoctorDataException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "doctors")
@Getter
@NoArgsConstructor
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String specialization;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Facility facility;

    public Doctor(String specialization) {
        this.specialization = specialization;
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
    }
    public void assignFacility(Facility facility) {
        this.facility = facility;
    }

    public void removeFacility() {
        this.facility = null;
    }
}
