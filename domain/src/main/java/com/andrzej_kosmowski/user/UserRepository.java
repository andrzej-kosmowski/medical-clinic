package com.andrzej_kosmowski.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(long id);
    List<User> findAll(int page, int size);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, long id);
    User save(User user);
    void delete(User user);
}
