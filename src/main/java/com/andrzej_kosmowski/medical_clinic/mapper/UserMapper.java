package com.andrzej_kosmowski.medical_clinic.mapper;

import com.andrzej_kosmowski.medical_clinic.dto.CreateUserCommand;
import com.andrzej_kosmowski.medical_clinic.dto.UserDto;
import com.andrzej_kosmowski.medical_clinic.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User from(CreateUserCommand command);
    UserDto toDto(User user);
}
