package com.andrzej_kosmowski.visit;

import com.andrzej_kosmowski.doctor.DoctorEntity;
import com.andrzej_kosmowski.patient.PatientEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "visits")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VisitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDateTime startTime;
    @Column(nullable = false)
    private LocalDateTime endTime;
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private DoctorEntity doctor;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientEntity patient;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitEntity visit)) return false;
        return id != null && id.equals(visit.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
