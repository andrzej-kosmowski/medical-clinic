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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/patients", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Patient management endpoints")
public class PatientController {
    private final PatientService patientService;

    @Operation(summary = "Get all patients")
    @ApiResponse(responseCode = "200", description = "List of patients returned successfully")
    @GetMapping
    public List<PatientDto> getAll() {
        return patientService.getAllPatients();
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
    public PatientDto addPatient(@RequestBody CreatePatientCommand patient) {
        return patientService.addPatient(patient);
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
        patientService.deletePatient(id);
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
    public PatientDto updatePatient(@PathVariable Long id, @RequestBody UpdatePatientCommand patient) {
        return patientService.updatePatient(id, patient);
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
        patientService.changePassword(id, password);
    }
}
