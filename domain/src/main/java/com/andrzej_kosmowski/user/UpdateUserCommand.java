package com.andrzej_kosmowski.user;

public record UpdateUserCommand(
        String firstName,
        String lastName,
        String email
) {
}
