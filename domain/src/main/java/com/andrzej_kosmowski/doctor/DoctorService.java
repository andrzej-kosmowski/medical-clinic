package com.andrzej_kosmowski.doctor;

import com.andrzej_kosmowski.facility.Facility;
import com.andrzej_kosmowski.facility.FacilityUseCase;
import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import com.andrzej_kosmowski.user.CreateUserCommand;
import com.andrzej_kosmowski.user.UpdateUserCommand;
import com.andrzej_kosmowski.user.User;
import com.andrzej_kosmowski.user.UserUseCase;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
public class DoctorService implements DoctorUseCase {
    private final DoctorRepository doctorRepository;
    private final UserUseCase userUseCase;
    private final FacilityUseCase facilityUseCase;

    @Override
    public Doctor getById(long id) {
        return findDoctorOrThrow(id);
    }

    @Override
    public PageResult<Doctor> findAll(PageQuery query) {
        if (query.page() < 0 || query.size() < 1) {
            throw new IllegalArgumentException("Invalid page or size");
        }
        return doctorRepository.findAll(query);
    }

    @Override
    public List<Doctor> getBySpecialization(String specialization) {
        return doctorRepository.findBySpecialization(specialization);
    }

    @Override
    public List<Doctor> getByFacilityId(Long facilityId) {
        facilityUseCase.getById(facilityId);
        return doctorRepository.findByFacilityId(facilityId);
    }

    @Override
    public Doctor create(CreateDoctorCommand command) {
        Set<Long> facilityIds = command.facilityIds() == null ? Set.of() : command.facilityIds();
        facilityIds.forEach(facilityUseCase::getById);
        User user = userUseCase.create(new CreateUserCommand(
                command.firstName(), command.lastName(), command.email(), command.password()));
        Doctor doctor = new Doctor(null, command.specialization(), user.getId(), facilityIds);
        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor update(long id, UpdateDoctorCommand command) {
        Doctor doctor = findDoctorOrThrow(id);
        userUseCase.update(doctor.getUserId(), new UpdateUserCommand(command.firstName(), command.lastName(),
                command.email()));
        doctor.updateSpecialization(command.specialization());
        return  doctorRepository.save(doctor);
    }

    @Override
    public Doctor assignFacility(long doctorId, long facilityId) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        facilityUseCase.getById(facilityId);
        doctor.assignFacility(facilityId);
        return doctorRepository.save(doctor);
    }

    @Override
    public List<Facility> getFacilities(long doctorId) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        return doctor.getFacilityIds().stream()
                .map(facilityUseCase::getById)
                .toList();
    }

    @Override
    public void removeFacility(long doctorId, long facilityId) {
        Doctor doctor = findDoctorOrThrow(doctorId);
        facilityUseCase.getById(facilityId);
        doctor.removeFacility(facilityId);
        doctorRepository.save(doctor);
    }

    @Override
    public void delete(long id) {
        Doctor doctor = findDoctorOrThrow(id);
        doctorRepository.delete(doctor);
    }

    private Doctor findDoctorOrThrow(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new DoctorNotFoundException(id));
    }
}
