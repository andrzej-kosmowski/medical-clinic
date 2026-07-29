package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.*;
import com.andrzej_kosmowski.medical_clinic.service.DoctorService;
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
@RequestMapping(value = "/doctors", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Doctors management endpoints")
public class DoctorController {
    private final DoctorService doctorService;

    @Operation(summary = "Get all doctors")
    @ApiResponse(responseCode = "200", description = "List of doctors returned successfully")
    @GetMapping
    public List<DoctorDto> getAll() {
        return doctorService.getAllDoctors();
    }

    @Operation(summary = "Get doctor by email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor found"),
        @ApiResponse(responseCode = "404", description = "Doctor not found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{email}")
    public DoctorDto getByEmail(@PathVariable String email) {
        return doctorService.getDoctorByEmail(email);
    }

    @Operation(summary = "Get doctor facility")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facility found"),
            @ApiResponse(responseCode = "404", description = "Facility not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{email}/facility")
    public FacilityDto getFacility(@PathVariable String email) {
        return doctorService.getFacility(email);
    }


    @Operation(summary = "Get all doctors from facility")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of doctors returned successfully")
    })
    @GetMapping("/facility/{facilityName}")
    public List<DoctorDto> getAllDoctors(@PathVariable String facilityName) {
        return doctorService.getDoctorsByFacility(facilityName);
    }

    @Operation(summary = "Create new doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Doctor created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid doctor data",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "409", description = "User already exists",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DoctorDto create(@RequestBody CreateDoctorCommand command) {
        return doctorService.addDoctor(command);
    }

    @Operation(summary = "Update doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor updated successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid doctor data",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping("/{email}")
    public DoctorDto update(@PathVariable String email, @RequestBody UpdateDoctorCommand command) {
        return doctorService.updateDoctorByEmail(email, command);
    }

    @Operation(summary = "Delete doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{email}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String email) {
        doctorService.deleteDoctorByEmail(email);
    }

    @Operation(summary = "Assign facility to doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Facility assigned successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor or facility not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping("/{email}/facility")
    public DoctorDto assignFacility(
            @PathVariable String email,
            @RequestBody AssignFacilityCommand command
    ) {
        return doctorService.assignFacility(email, command);
    }

    @Operation(summary = "Remove facility from doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Facility removed successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{email}/facility")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFacility(@PathVariable String email) {
        doctorService.removeFacility(email);
    }
}
