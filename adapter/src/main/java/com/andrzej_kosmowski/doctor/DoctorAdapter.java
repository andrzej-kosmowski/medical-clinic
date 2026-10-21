package com.andrzej_kosmowski.doctor;

import com.andrzej_kosmowski.facility.Facility;
import com.andrzej_kosmowski.facility.FacilityEntity;
import com.andrzej_kosmowski.facility.FacilityJpaRepository;
import com.andrzej_kosmowski.facility.FacilityNotFoundException;
import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import com.andrzej_kosmowski.user.UserEntity;
import com.andrzej_kosmowski.user.UserJpaRepository;
import com.andrzej_kosmowski.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DoctorAdapter implements DoctorRepository {
    private final DoctorJpaRepository doctorJpaRepository;
    private final FacilityJpaRepository facilityJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final DoctorMapper mapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<Doctor> findById(long id) {
        return doctorJpaRepository.findById(id).map(mapper::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Doctor> findAll(PageQuery query) {
        Page<DoctorEntity> page = doctorJpaRepository.findAll(PageRequest.of(query.page(), query.size()));
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

    @Transactional(readOnly = true)
    @Override
    public List<Doctor> findBySpecialization(String specialization) {
        return doctorJpaRepository.findAllBySpecializationIgnoreCase(specialization)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Doctor> findByFacilityId(long facilitiesId) {
        return doctorJpaRepository.findAllByFacilitiesId(facilitiesId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Transactional
    @Override
    public Doctor save(Doctor doctor) {
        DoctorEntity entity = mapper.toEntity(doctor);
        UserEntity user = userJpaRepository.findById(doctor.getUserId())
                .orElseThrow(() -> new UserNotFoundException(doctor.getUserId()));
        entity.setUser(user);
        Set<Long> requestedIds = doctor.getFacilityIds();
        List<FacilityEntity> foundFacilities = facilityJpaRepository.findAllById(requestedIds);
        Set<Long> foundIds = foundFacilities.stream()
                .map(FacilityEntity::getId)
                .collect(Collectors.toSet());
        Set<Long> missingIds = new HashSet<>(requestedIds);
        missingIds.removeAll(foundIds);
        if (!missingIds.isEmpty()) {
            throw new FacilityNotFoundException(missingIds);
        }
        entity.setFacilities(new HashSet<>(foundFacilities));
        return mapper.toDomain(doctorJpaRepository.save(entity));
    }
}
