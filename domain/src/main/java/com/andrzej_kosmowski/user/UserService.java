package com.andrzej_kosmowski.user;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class UserService implements UserUseCase {
    private final UserRepository userRepository;

    @Override
    public User getById(long id) {
        return findUserOrThrow(id);
    }

    @Override
    public PageResult<User> getAll(PageQuery query) {
        if (query.page() < 0 || query.size() < 1) {
            throw new IllegalArgumentException("Invalid page or size");
        }
        return userRepository.findAll(query);
    }

    @Override
    public User create(CreateUserCommand command) {
        log.info("Creating user: email={}", command.email());
        if (userRepository.existsByEmail(command.email())) {
            throw new UserAlreadyExistsException(command.email());
        }
        User user = new User(null, command.firstName(), command.lastName(), command. email(), command.password());
        user.validate();
        log.info("User created successfully: id={}, email={}", user.getId(), user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public User update(long id, UpdateUserCommand command) {
        log.info("Updating user: id={}, email={}", id, command.email());
        User user = findUserOrThrow(id);
        this.validateEmailChange(user, command.email());
        user.update(command.firstName(), command.lastName(), command.email());
        log.info("User updated successfully: id={}, email={}", user.getId(), user.getEmail());
        return userRepository.save(user);
    }

    @Override
    public void changePassword(long id, String newPassword) {
        log.info("Changing password for user: id={}", id);
        User user = findUserOrThrow(id);
        user.changePassword(newPassword);
        userRepository.save(user);
        log.info("Password changed successfully: userId={}", user.getId());
    }

    @Override
    public void delete(long id) {
        log.info("Deleting user: id={}", id);
        User user = findUserOrThrow(id);
        userRepository.delete(user);
        log.info("User deleted successfully: id={}, email={}", id, user.getEmail());
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public void validateEmailChange(User user, String newEmail) {
        if (userRepository.existsByEmailAndIdNot(newEmail, user.getId())) {
            throw new UserAlreadyExistsException(newEmail);
        }
    }
}
