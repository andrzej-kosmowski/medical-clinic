package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.*;
import com.andrzej_kosmowski.medical_clinic.dto.patient.CreatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.patient.PatientDto;
import com.andrzej_kosmowski.medical_clinic.dto.patient.UpdatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.ChangePasswordCommand;
import com.andrzej_kosmowski.medical_clinic.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/patients", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Patient management endpoints")
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get all patients")
    @ApiResponse(responseCode = "200", description = "List of patients returned successfully")
    @GetMapping
    public PageResponse<PatientDto> getAll(Pageable pageable) {
        return patientService.getAllPatients(pageable);
    }

    @Operation(summary = "Get patient by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient found"),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{id}")
    public PatientDto getPatient(@PathVariable Long id) {
        return patientService.getPatientById(id);
    }

    @Operation(summary = "Create a new patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Patient created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid patient data",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "409", description = "Patient already exists",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PatientDto addPatient(@RequestBody CreatePatientCommand command) {
        log.info("Adding new patient={}", command.email());
        PatientDto patient = patientService.addPatient(command);
        log.info("New patient={} added with id={}", patient.email(), patient.idCardNo());
        return patient;
    }

    @Operation(summary = "Delete patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePatient(@PathVariable Long id) {
        log.info("Deleting patient={}", id);
        patientService.deletePatient(id);
        log.info("Patient deleted successfully with id={}", id);
    }

    @Operation(summary = "Update patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Patient updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid patient data",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping("/{id}")
    public PatientDto updatePatient(@PathVariable Long id, @RequestBody UpdatePatientCommand command) {
        log.info("Updating patient={} with id={}", command.email(), id);
        PatientDto patient = patientService.updatePatient(id, command);
        log.info("Patient updated successfully with id={}", id);
        return patient;
    }

    @Operation(summary = "Change patient password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid password",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "404", description = "Patient not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePatientPassword(@PathVariable Long id, @RequestBody ChangePasswordCommand password) {
        log.info("Changing patient={} id", id);
        patientService.changePassword(id, password);
        log.info("Patient password changed successfully with id={}", id);
    }
}
