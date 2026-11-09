package com.andrzej_kosmowski.user;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserAdapter implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> findById(long id) {
        return userJpaRepository.findById(id).map(userMapper::toDomain);
    }

    @Override
    public PageResult<User> findAll(PageQuery query) {
        Page<UserEntity> page = userJpaRepository.findAll(
                PageRequest.of(query.page(), query.size())
        );

        return new PageResult<>(
                page.getContent().stream()
                        .map(userMapper::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByEmailAndIdNot(String email, long id) {
        return userJpaRepository.existsByEmailAndIdNot(email, id);
    }

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        return userMapper.toDomain(userJpaRepository.save(entity));
    }

    @Override
    public void delete(User user) {
        userJpaRepository.deleteById(user.getId());
    }
}
