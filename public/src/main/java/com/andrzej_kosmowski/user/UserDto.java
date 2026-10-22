package com.andrzej_kosmowski.user;

public record UserDto(
        Long id,
        String firstName,
        String lastName,
        String email
) {
}
