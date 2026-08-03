package com.andrzej_kosmowski.medical_clinic.model;

import com.andrzej_kosmowski.medical_clinic.exception.visit.InvalidVisitDataException;
import com.andrzej_kosmowski.medical_clinic.exception.visit.PastVisitException;
import com.andrzej_kosmowski.medical_clinic.exception.visit.VisitAlreadyBookedException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "visits")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public Visit (LocalDateTime startTime, LocalDateTime endTime, Doctor doctor) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.doctor = doctor;
    }

    public void assignDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public void assignPatient(Patient patient) {
        if (!isAvailable()) {
            throw new VisitAlreadyBookedException(id);
        }
        if (!startTime.isAfter(LocalDateTime.now())) {
            throw new PastVisitException(startTime);
        }
        this.patient = patient;
    }

    public boolean isAvailable() {
        return patient == null;
    }

    public void cancelVisit() {
        if (patient == null) {
            throw new InvalidVisitDataException("Visit has no patient");
        }
        this.patient = null;
    }

    public void validate() {
        if (doctor == null) {
            throw new InvalidVisitDataException("Visit must be assign to a doctor");
        }
        if (startTime == null || endTime == null) {
            throw new InvalidVisitDataException("Start and end time cannot be empty");
        }
        if (!endTime.isAfter(startTime)) {
            throw new InvalidVisitDataException("End date must be after start date");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new InvalidVisitDataException("Cannot create a visit in the past");
        }
        if (!isFullQuarterHour(startTime)) {
            throw new InvalidVisitDataException("Start time must be on full quarter hour");
        }
        if (!isFullQuarterHour(endTime)) {
            throw new InvalidVisitDataException("End time must be on full quarter hour");
        }
    }
    private boolean isFullQuarterHour(LocalDateTime time) {
        return time.getMinute() % 15 == 0 && time.getSecond() == 0 && time.getNano() == 0;
    }
}
