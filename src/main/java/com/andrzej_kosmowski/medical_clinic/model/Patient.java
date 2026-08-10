package com.andrzej_kosmowski.medical_clinic.model;

import com.andrzej_kosmowski.medical_clinic.dto.patient.UpdatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.exception.patient.InvalidPatientDataException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String idCardNo;
    private String phoneNumber;
    private LocalDate birthday;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @OneToMany(mappedBy = "patient")
    private List<Visit> visits = new ArrayList<>();

    public Patient(String idCardNo, String phoneNumber, LocalDate birthday) {
        this.idCardNo = idCardNo;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
    }

    public void assignUser(User user) {
        this.user = user;
        user.assignPatient(this);
    }

    public void addVisit(Visit visit) {
        if (!visits.contains(visit)) {
            visits.add(visit);
        }
    }

    public void removeVisit(Visit visit) {
        visits.remove(visit);
    }

    public void validate() {
        if (idCardNo == null || idCardNo.isBlank()) {
            throw new InvalidPatientDataException("idCardNo cannot be empty");
        }
        if (phoneNumber != null && !phoneNumber.matches("^\\d{9}$")) {
            throw new InvalidPatientDataException("Phone must have 9 digits");
        }
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new InvalidPatientDataException("Birth date must be after the current date");
        }
    }

    public void update(UpdatePatientCommand command) {
        this.phoneNumber = command.phoneNumber();
        this.birthday = command.birthday();
        this.idCardNo = command.idCardNo();
        this.validate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patient patient)) return false;
        return id != null && id.equals(patient.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}