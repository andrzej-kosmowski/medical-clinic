package com.andrzej_kosmowski.patient;

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

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PatientAdapter implements PatientRepository {
    private final PatientJpaRepository patientJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final PatientMapper patientMapper;

    @Override
    public Optional<Patient> findById(long id) {
        return patientJpaRepository.findById(id).map(patientMapper::toDomain);
    }

    @Override
    public PageResult<Patient> findAll(PageQuery query) {
        Page<PatientEntity> page = patientJpaRepository.findAll(
                PageRequest.of(query.page(), query.size())
        );
        return new PageResult<>(
                page.getContent().stream()
                        .map(patientMapper::toDomain)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    public boolean existsByIdCardNo(String idCardNo) {
        return patientJpaRepository.existsByIdCardNo(idCardNo);
    }

    @Override
    public boolean existsByIdCardNoAndIdNot(String idCardNo, long id) {
        return patientJpaRepository.existsByIdCardNoAndIdNot(idCardNo, id);
    }

    @Override
    @Transactional
    public Patient save(Patient patient) {
        PatientEntity patientEntity = patientMapper.toEntity(patient);
        UserEntity user = userJpaRepository.findById(patient.getUserId())
                .orElseThrow(() -> new UserNotFoundException(patient.getUserId()));
        patientEntity.setUser(user);
        return patientMapper.toDomain(patientJpaRepository.save(patientEntity));
    }

    @Override
    public void delete(Patient patient) {
        patientJpaRepository.deleteById(patient.getId());
    }
}
