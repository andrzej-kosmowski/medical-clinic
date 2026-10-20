package com.andrzej_kosmowski.facility;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;

public interface FacilityUseCase {
    Facility getById(long id);
    PageResult<Facility> getAll(PageQuery query);
    Facility create(CreateFacilityCommand command);
    Facility update(long id, UpdateFacilityCommand command);
}
