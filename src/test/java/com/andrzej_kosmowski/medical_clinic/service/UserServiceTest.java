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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {
    UserService userService;
    UserRepository userRepository;
    UserMapper userMapper;

    @BeforeEach
    void setUp() {
        this.userRepository = Mockito.mock(UserRepository.class);
        this.userMapper = Mappers.getMapper(UserMapper.class);
        this.userService = new UserService(userRepository, userMapper);
    }

    @Test
    void getAllUsers_UsersExist_UsersReturned() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<User> users = List.of(
            new User("Jan", "Kowalski", "jan@test.pl", "pass123"),
            new User("Anna", "Nowak", "anna@test.pl", "pass321")
        );
        Page<User> page = new PageImpl<>(users);
        when(userRepository.findAll(pageable)).thenReturn(page);
        // when
        PageResponse<UserDto> result = userService.getAllUsers(pageable);
        // then
        Assertions.assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("Jan", result.content().get(0).firstName()),
                () -> assertEquals("Anna", result.content().get(1).firstName()),
                () -> assertEquals("Kowalski", result.content().get(0).lastName()),
                () -> assertEquals("Nowak", result.content().get(1).lastName()),
                () -> assertEquals("jan@test.pl", result.content().get(0).email()),
                () -> assertEquals("anna@test.pl", result.content().get(1).email())
        );
    }

    @Test
    void getUserById_UserExist_UserReturned() {
        // given
        User user = new User("Jan", "Kowalski", "jan@test.pl", "pass123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // when
        UserDto result = userService.getUserById(1L);
        // then
        Assertions.assertAll(
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertEquals("jan@test.pl", result.email())

        );
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsException() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("User with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void addUser_ValidCommand_UserCreated() {
        // given
        CreateUserCommand command = new CreateUserCommand("Jan", "Kowalski",
                "jan@test.pl",  "pass123");
        when(userRepository.existsByEmail("jan@test.pl")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        // when
        UserDto result = userService.addUser(command);
        // then
        Assertions.assertAll(
                () -> assertEquals("Jan", result.firstName()),
                () -> assertEquals("Kowalski", result.lastName()),
                () -> assertEquals("jan@test.pl", result.email())
        );
    }

    @Test
    void addUser_UserAlreadyExists_ThrowsException() {
        // given
        CreateUserCommand command = new CreateUserCommand("Jan", "Kowalski",
                "jan@test.pl",  "pass123");
        when(userRepository.existsByEmail("jan@test.pl")).thenReturn(true);
        // when
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.addUser(command));
        // then
        Assertions.assertAll(
                () -> assertEquals("User with email jan@test.pl already exists", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void createUser_ValidCommand_UserCreated() {
        // given
        CreateUserCommand command = new CreateUserCommand("Jan", "Kowalski",
                "jan@test.pl",  "pass123");
        when(userRepository.existsByEmail("jan@test.pl")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        // when
        User result = userService.createUser(command);
        // then
        Assertions.assertAll(
                () -> assertEquals("Jan", result.getFirstName()),
                () -> assertEquals("Kowalski", result.getLastName()),
                () -> assertEquals("jan@test.pl", result.getEmail())
        );
    }

    @Test
    void createUser_UserAlreadyExists_ThrowsException() {
        // given
        CreateUserCommand command = new CreateUserCommand("Jan", "Kowalski",
                "jan@test.pl",  "pass123");
        when(userRepository.existsByEmail("jan@test.pl")).thenReturn(true);
        // when
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.createUser(command));
        // then
        Assertions.assertAll(
                () -> assertEquals("User with email jan@test.pl already exists", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void updateUser_ValidCommand_UserUpdated() {
        // given
        User user = new User("Jan", "Kowalski", "jan@test.pl", "pass123");
        UpdateUserCommand command = new UpdateUserCommand("Adam", "Nowak", "adam@test.pl");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("adam@test.pl", user.getId())).thenReturn(false);
        // when
        UserDto result = userService.updateUser(1L, command);
        // then
        Assertions.assertAll(
                () -> assertEquals("Adam", result.firstName()),
                () -> assertEquals("Nowak", result.lastName()),
                () -> assertEquals("adam@test.pl", result.email())
        );
    }

    @Test
    void updateUser_UserDoesNotExist_ThrowsException() {
        // given
        UpdateUserCommand command = new UpdateUserCommand("Adam", "Nowak", "adam@test.pl");
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(1L, command));
        // then
        Assertions.assertAll(
                () -> assertEquals("User with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void updateUser_EmailAlreadyExists_ThrowsException() {
        // given
        User user = new User("Jan", "Kowalski", "jan@test.pl", "pass123");
        UpdateUserCommand command = new UpdateUserCommand("Adam", "Nowak", "adam@test.pl");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("adam@test.pl", user.getId())).thenReturn(true);
        // when
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.updateUser(1L, command));
        // then
        Assertions.assertAll(
                () -> assertEquals("User with email adam@test.pl already exists", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }

    @Test
    void deleteUser_UserExists_UserDeleted() {
        // given
        User user = new User("Jan", "Kowalski", "jan@test.pl", "pass123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        // when
        userService.deleteUser(1L);
        // then
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_UserDoesNotExist_ThrowsException() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        // when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(1L));
        // then
        Assertions.assertAll(
                () -> assertEquals("User with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void changePassword_UserExists_PasswordChanged() {
        // given
        User user = new User("Jan", "Kowalski", "jan@test.pl", "pass123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        ChangePasswordCommand command = new ChangePasswordCommand("newPass123");
        // when
        userService.changePassword(1L, command);
        // then
        assertEquals("newPass123", user.getPassword());
    }

    @Test
    void changePassword_UserDoesNotExist_ThrowsException() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        ChangePasswordCommand command = new ChangePasswordCommand("newPass123");
        // when
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.changePassword(1L, command));
        // then
        Assertions.assertAll(
                () -> assertEquals("User with id 1 not found", exception.getMessage()),
                () -> assertEquals(HttpStatus.NOT_FOUND, exception.getStatus())
        );
    }

    @Test
    void validateEmailChange_EmailAvailable_NoExceptionThrown() {
        // given
        User user = new User("Jan", "Kowalski", "jan@test.pl", "pass123");
        when(userRepository.existsByEmailAndIdNot(anyString(), any())).thenReturn(false);
        // when & then
        assertDoesNotThrow(() -> userService.validateEmailChange(user, "new@test.pl"));
    }

    @Test
    void validateEmailChange_EmailAlreadyExists_ThrowsException() {
        // given
        User user = new User("Jan", "Kowalski", "jan@test.pl", "pass123");
        when(userRepository.existsByEmailAndIdNot(anyString(), any())).thenReturn(true);
        // when
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.validateEmailChange(user, "new@test.pl"));
        // then
        Assertions.assertAll(
                () -> assertEquals("User with email new@test.pl already exists", exception.getMessage()),
                () -> assertEquals(HttpStatus.CONFLICT, exception.getStatus())
        );
    }
}