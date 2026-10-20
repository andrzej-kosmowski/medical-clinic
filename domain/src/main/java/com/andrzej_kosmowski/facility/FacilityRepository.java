package com.andrzej_kosmowski.facility;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import java.util.Optional;

public interface FacilityRepository {
    Optional<Facility> findById(long id);
    PageResult<Facility> findAll(PageQuery query);
    boolean existsByName(String name);
    Facility save(Facility facility);
}
