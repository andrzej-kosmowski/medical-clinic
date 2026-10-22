package com.andrzej_kosmowski.user;

public record CreateUserCommand(
        String firstName,
        String lastName,
        String email,
        String password
) {
}
