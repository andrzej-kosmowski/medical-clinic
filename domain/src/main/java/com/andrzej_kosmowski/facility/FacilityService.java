package com.andrzej_kosmowski.facility;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FacilityService implements FacilityUseCase {
    private final FacilityRepository facilityRepository;

    @Override
    public Facility getById(long id) {
        return findFacilityOrThrow(id);
    }

    @Override
    public PageResult<Facility> getAll(PageQuery query) {
        if (query.page() < 0 || query.size() < 1) {
            throw new IllegalArgumentException("Invalid page or size");
        }
        return facilityRepository.findAll(query);
    }

    @Override
    public Facility create(CreateFacilityCommand command) {
        if (facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        Facility facility = new Facility(null, command.name(), command.city(), command.zipCode(), command.street(),
                command.buildingNumber());
        facility.validate();
        return facilityRepository.save(facility);
    }

    @Override
    public Facility update(long id, UpdateFacilityCommand command) {
        Facility facility = findFacilityOrThrow(id);
        if (!facility.getName().equals(command.name())
                && facilityRepository.existsByName(command.name())) {
            throw new FacilityAlreadyExistsException(command.name());
        }
        facility.update(command.name(), command.city(), command.zipCode(), command.street(), command.buildingNumber());
        return facilityRepository.save(facility);
    }

    @Override
    public void delete(long id) {
        Facility facility = findFacilityOrThrow(id);
        facilityRepository.delete(facility);
    }

    private Facility findFacilityOrThrow(Long id) {
        return facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityNotFoundException(id));
    }
}
