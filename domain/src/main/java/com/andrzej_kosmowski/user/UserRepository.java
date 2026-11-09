package com.andrzej_kosmowski.user;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long id);
    PageResult<User> findAll(PageQuery query);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, long id);
    User save(User user);
    void delete(User user);
}
