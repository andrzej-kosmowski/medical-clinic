package com.andrzej_kosmowski.patient;

import com.andrzej_kosmowski.ErrorMessageDto;
import com.andrzej_kosmowski.pagination.PageQuery;
import com.andrzej_kosmowski.pagination.PageResult;
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
    private final PatientMapper patientMapper;

    @Operation(summary = "Get all patients")
    @ApiResponse(responseCode = "200", description = "List of patients returned successfully")
    @GetMapping
    public PageResult<PatientDto> getAll(Pageable pageable) {
        PageQuery query = new PageQuery(pageable.getPageNumber(), pageable.getPageSize());
        return patientService.getAll(query).map(patientMapper::toDto);
    }

    @Operation(summary = "Get patient by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient found"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{id}")
    public PatientDto getById(@PathVariable Long id) {
        return patientMapper.toDto(patientService.getById(id));
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
    public PatientDto create(@RequestBody CreatePatientCommand command) {
        log.info("Creating patient with idCardNo={}", command.idCardNo());
        Patient patient = patientService.create(command);
        PatientDto response = patientMapper.toDto(patient);
        log.info("Created patient with id={}", response.id());
        return response;
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
    public PatientDto update(@PathVariable Long id, @RequestBody UpdatePatientCommand command) {
        log.info("Updating patient with id={}", id);
        Patient patient = patientService.update(id, command);
        PatientDto response = patientMapper.toDto(patient);
        log.info("Updated patient with id={}", response.id());
        return response;
    }

    @Operation(summary = "Delete patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Deleting patient with id={}", id);
        patientService.delete(id);
        log.info("Deleted patient with id={}", id);
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
    public void changePassword(
            @PathVariable Long id,
            @RequestBody ChangePatientPasswordRequest request
    ) {
        log.info("Changing password for patient id={}", id);
        patientService.changePassword(id, request.password());
        log.info("Changed password for patient id={}", id);
    }
}