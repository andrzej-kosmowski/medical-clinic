package com.andrzej_kosmowski.patient;

import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
import com.andrzej_kosmowski.user.CreateUserCommand;
import com.andrzej_kosmowski.user.UpdateUserCommand;
import com.andrzej_kosmowski.user.User;
import com.andrzej_kosmowski.user.UserUseCase;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PatientService implements PatientUseCase {
    private final PatientRepository patientRepository;
    private final UserUseCase userUseCase;
    @Override
    public Patient getById(long id) {
        return findPatientOrThrow(id);
    }

    @Override
    public PageResult<Patient> getAll(PageQuery query) {
        if (query.page() < 0 || query.size() < 1) {
            throw new IllegalArgumentException("Invalid page or size");
        }
        return patientRepository.findAll(query);
    }

    @Override
    public Patient create(CreatePatientCommand command) {
        if (patientRepository.existsByIdCardNo(command.idCardNo())) {
            throw new PatientAlreadyExistsException(command.idCardNo());
        }
        User user = userUseCase.create(new CreateUserCommand(command.firstName(), command.lastName(),
                command.email(), command.password()));
        Patient patient = new Patient(null, command.idCardNo(), command.phoneNumber(), command.birthday(),
                user.getId());
        patient.validate();
        return patientRepository.save(patient);
    }

    @Override
    public Patient update(long id, UpdatePatientCommand command) {
        Patient patient = findPatientOrThrow(id);
        if (!patient.getIdCardNo().equals(command.idCardNo())
                && patientRepository.existsByIdCardNoAndIdNot(command.idCardNo(), id)) {
            throw new PatientAlreadyExistsException(command.idCardNo());
        }
        patient.update(command.phoneNumber(), command.birthday(), command.idCardNo());
        userUseCase.update(
                patient.getUserId(),
                new UpdateUserCommand(command.firstName(), command.lastName(), command.email())
        );
        return patientRepository.save(patient);
    }

    @Override
    public void changePassword(long id, String newPassword) {
        Patient patient = findPatientOrThrow(id);
        userUseCase.changePassword(patient.getUserId(), newPassword);
    }

    @Override
    public void delete(long id) {
        Patient patient = findPatientOrThrow(id);
        patientRepository.delete(patient);
    }

    private Patient findPatientOrThrow(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new PatientNotFoundException(id));
    }
}
