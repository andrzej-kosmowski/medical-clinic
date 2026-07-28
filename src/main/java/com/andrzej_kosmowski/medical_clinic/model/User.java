package com.andrzej_kosmowski.medical_clinic.model;

import com.andrzej_kosmowski.medical_clinic.exception.InvalidPatientDataException;
import com.andrzej_kosmowski.medical_clinic.exception.InvalidUserDataException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToOne(mappedBy = "user")
    private Patient patient;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public void updateEmail(String email) {
        this.email = email;
        this.validate();
    }

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new InvalidUserDataException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new InvalidUserDataException("Invalid email");
        }
        if (password == null || password.length() < 6) {
            throw new InvalidUserDataException("Password must have at least 6 characters");
        }
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new InvalidUserDataException("Password must have at least 6 characters");
        }
        this.password = newPassword;
    }
}
