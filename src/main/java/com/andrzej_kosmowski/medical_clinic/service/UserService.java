package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.user.ChangePasswordCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.CreateUserCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.UpdateUserCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.UserDto;
import com.andrzej_kosmowski.medical_clinic.exception.user.UserAlreadyExistsException;
import com.andrzej_kosmowski.medical_clinic.exception.user.UserNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.UserMapper;
import com.andrzej_kosmowski.medical_clinic.model.User;
import com.andrzej_kosmowski.medical_clinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUserByEmail(String email) {
        User user = findUserOrThrow(email);

        return userMapper.toDto(user);
    }

    public UserDto addUser(CreateUserCommand command) {;
        return userMapper.toDto(createUser(command));
    }

    public User createUser(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }
        User user = userMapper.from(command);
        user.validate();
        return userRepository.save(user);
    }

    public UserDto updateUser(String email,UpdateUserCommand command) {
        User user = findUserOrThrow(email);
        this.validateEmailChange(user, command.email());
        user.update(command.firstName(), command.lastName(), command.email());
        User updated = userRepository.save(user);
        return userMapper.toDto(updated);
    }

    public void deleteUser(String email) {
        User user = findUserOrThrow(email);
        userRepository.delete(user);
    }

    public void changePassword(String email, ChangePasswordCommand command) {
        User user = findUserOrThrow(email);
        user.changePassword(command.password());
        userRepository.save(user);
    }

    public void validateEmailChange(User user, String newEmail) {
        if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            throw new UserAlreadyExistsException(newEmail);
        }
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
