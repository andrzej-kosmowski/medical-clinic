package com.andrzej_kosmowski.user;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;

import java.util.List;

public interface UserUseCase {
    User getById(long id);
    PageResult<User> getAll(PageQuery query);
    User create(CreateUserCommand command);
    User update(long id, UpdateUserCommand command);
    void changePassword(long id, String newPassword);
    void delete(long id);
}
