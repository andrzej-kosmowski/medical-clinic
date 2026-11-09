package com.andrzej_kosmowski.visit;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Visit {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long doctorId;
    private Long patientId;

    public Visit (Long id, LocalDateTime startTime, LocalDateTime endTime, Long doctorId, Long patientId) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.doctorId = doctorId;
        this.patientId = patientId;
    }

    public void assignPatient(Long patientId) {
        if (patientId == null || patientId < 1) {
            throw new InvalidVisitDataException("Patient id must be positive");
        }
        if (!isAvailable()) {
            throw new VisitAlreadyBookedException(id);
        }
        if (!startTime.isAfter(LocalDateTime.now())) {
            throw new PastVisitException(startTime);
        }

        this.patientId = patientId;
    }

    public boolean isAvailable() {
        return patientId == null;
    }

    public void cancelVisit() {
        if (isAvailable()) {
            throw new InvalidVisitDataException("Visit has no patient");
        }
        this.patientId = null;
    }

    public void validate() {
        if (doctorId == null) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visit visit)) return false;
        return id != null && id.equals(visit.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
