package com.andrzej_kosmowski.medical_clinic.service;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.user.ChangePasswordCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.CreateUserCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.UpdateUserCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.UserDto;
import com.andrzej_kosmowski.medical_clinic.exception.user.UserAlreadyExistsException;
import com.andrzej_kosmowski.medical_clinic.exception.user.UserNotFoundException;
import com.andrzej_kosmowski.medical_clinic.mapper.UserMapper;
import com.andrzej_kosmowski.medical_clinic.model.User;
import com.andrzej_kosmowski.medical_clinic.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public PageResponse<UserDto> getAllUsers(Pageable pageable) {
        return PageResponse.from(userRepository.findAll(pageable)
                .map(userMapper::toDto));
    }

    public UserDto getUserById(Long id) {
        User user = findUserOrThrow(id);
        return userMapper.toDto(user);
    }

    public UserDto addUser(CreateUserCommand command) {
        return userMapper.toDto(createUser(command));
    }

    @Transactional
    public User createUser(CreateUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }
        User user = userMapper.from(command);
        user.validate();
        return userRepository.save(user);
    }

    @Transactional
    public UserDto updateUser(Long id,UpdateUserCommand command) {
        User user = findUserOrThrow(id);
        this.validateEmailChange(user, command.email());
        user.update(command.firstName(), command.lastName(), command.email());
        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserOrThrow(id);
        userRepository.delete(user);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordCommand command) {
        User user = findUserOrThrow(id);
        user.changePassword(command.password());
    }

    public void validateEmailChange(User user, String newEmail) {
        if (userRepository.existsByEmailAndIdNot(newEmail, user.getId())) {
            throw new UserAlreadyExistsException(newEmail);
        }
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
