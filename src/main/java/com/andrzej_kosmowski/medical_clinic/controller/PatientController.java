package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.ChangePasswordCommand;
import com.andrzej_kosmowski.medical_clinic.dto.CreatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.PatientDto;
import com.andrzej_kosmowski.medical_clinic.dto.UpdatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @GetMapping
    public List<PatientDto> getAll() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{email}")
    public PatientDto getPatient(@PathVariable String email) {
        return patientService.getPatientByEmail(email);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody CreatePatientCommand patient) {
        return patientService.addPatient(patient);
    }

    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatientByEmail(@PathVariable String email) {
        patientService.deletePatientByEmail(email);
    }

    @PutMapping("/{email}")
    @ResponseStatus(HttpStatus.OK)
    public PatientDto updatePatientByEmail(@PathVariable String email, @RequestBody UpdatePatientCommand patient) {
        return patientService.updatePatientByEmail(email, patient);
    }

    @PatchMapping("/{email}/password")
    @ResponseStatus(HttpStatus.OK)
    public void changePatientPassword(@PathVariable String email, @RequestBody ChangePasswordCommand newPassword) {
        patientService.changePassword(email, newPassword);
    }
}
