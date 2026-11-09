package com.andrzej_kosmowski.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration {

    @Bean
    UserService userService(UserAdapter userAdapter) {
        return new UserService(userAdapter);
    }
}
