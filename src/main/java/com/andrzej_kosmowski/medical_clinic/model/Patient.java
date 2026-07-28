package com.andrzej_kosmowski.medical_clinic.model;

import com.andrzej_kosmowski.medical_clinic.dto.UpdatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.exception.InvalidPatientDataException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "patients")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String idCardNo;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String phoneNumber;

    private LocalDate birthday;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public void assignUser(User user) {
        this.user = Objects.requireNonNull(user);
    }

    public void validate() {
        if (idCardNo == null || idCardNo.isBlank()) {
            throw new InvalidPatientDataException("idCardNo cannot be empty");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidPatientDataException("First name cannot be empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new InvalidPatientDataException("Last name cannot be empty");
        }
        if (phoneNumber != null && !phoneNumber.matches("^\\d{9}$")) {
            throw new InvalidPatientDataException("Phone must have 9 digits");
        }
        if (birthday != null && birthday.isAfter(LocalDate.now())) {
            throw new InvalidPatientDataException("Birth date must be after the current date");
        }
    }

    public void update(UpdatePatientCommand command) {
        this.firstName = command.firstName();
        this.lastName = command.lastName();
        this.phoneNumber = command.phoneNumber();
        this.birthday = command.birthday();
        this.idCardNo = command.idCardNo();
        this.validate();
    }
}