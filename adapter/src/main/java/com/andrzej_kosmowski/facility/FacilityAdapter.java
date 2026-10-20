package com.andrzej_kosmowski.facility;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FacilityAdapter implements FacilityRepository {
    private final FacilityJpaRepository jpaRepository;
    private final FacilityMapper mapper;

    @Override
    public Optional<Facility> findById(long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public PageResult<Facility> findAll(PageQuery query) {
        Page<FacilityEntity> page = jpaRepository.findAll(PageRequest.of(query.page(), query.size()));
        return new PageResult<>(
                page.getContent().stream()
                        .map(mapper::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public Facility save(Facility facility) {
        if (facility.getId() != null && !jpaRepository.existsById(facility.getId())) {
            throw new FacilityNotFoundException(facility.getId());
        }
        FacilityEntity entity = mapper.toEntity(facility);
        return mapper.toDomain(jpaRepository.save(entity));
    }


}
