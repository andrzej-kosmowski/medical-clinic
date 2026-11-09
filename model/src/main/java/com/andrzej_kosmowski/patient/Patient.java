package com.andrzej_kosmowski.patient;

import lombok.*;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Patient {
    private Long id;
    private String idCardNo;
    private String phoneNumber;
    private LocalDate birthday;
    private Long userId;

    public Patient(Long id, String idCardNo, String phoneNumber, LocalDate birthday, Long userId) {
        this.id = id;
        this.idCardNo = idCardNo;
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.userId = userId;
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

    public void update(String phoneNumber, LocalDate birthday, String idCardNo) {
        this.phoneNumber = phoneNumber;
        this.birthday = birthday;
        this.idCardNo = idCardNo;
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