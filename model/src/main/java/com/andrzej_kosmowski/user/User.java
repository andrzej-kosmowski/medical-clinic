package com.andrzej_kosmowski.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public User(Long id, String firstName, String lastName, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public void update(String firstName, String lastName, String email) {
        this.validate();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public void validate() {
        if (email == null || email.isBlank()) {
            throw new InvalidUserDataException("Email cannot be empty");
        }
        if (!email.contains("@")) {
            throw new InvalidUserDataException("Invalid email");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidUserDataException("First name cannot be empty");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new InvalidUserDataException("Last name cannot be empty");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
