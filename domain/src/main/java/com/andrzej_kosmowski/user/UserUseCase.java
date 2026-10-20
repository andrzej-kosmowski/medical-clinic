package com.andrzej_kosmowski.user;

import java.util.List;

public interface UserUseCase {
    User getById(long id);
    List<User> getAll(int page, int size);
    User create(CreateUserCommand command);
    User update(long id, UpdateUserCommand command);
    void changePassword(long id, String newPassword);
    void delete(long id);
}
