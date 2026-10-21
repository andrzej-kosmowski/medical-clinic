package com.andrzej_kosmowski.user;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserEntity userEntity);
    UserEntity toEntity(User domainUser);
    UserDto toDto(User domainUser);
}
