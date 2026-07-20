package com.andrzej_kosmowski.medical_clinic.model;

import com.andrzej_kosmowski.medical_clinic.dto.UpdatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.exception.InvalidPatientDataException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    private String email;
    private String password;
    private String idCardNo;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate birthday;

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new InvalidPatientDataException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new InvalidPatientDataException("Invalid email");
        }
        if (password == null || password.length() < 6) {
            throw new InvalidPatientDataException("Password must have at least 6 characters");
        }
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
        this.email = command.email();
        this.firstName = command.firstName();
        this.lastName = command.lastName();
        this.phoneNumber = command.phoneNumber();
        this.birthday = command.birthday();
        this.idCardNo = command.idCardNo();
        this.validate();
    }

    public boolean hasSameEmail(Patient patient) {
        return patient != null
                && this.email != null
                && this.email.equalsIgnoreCase(patient.getEmail());
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new InvalidPatientDataException("Password must have at least 6 characters");
        }
        this.password = newPassword;
    }
}