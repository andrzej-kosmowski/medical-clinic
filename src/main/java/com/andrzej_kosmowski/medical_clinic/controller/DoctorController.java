package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.*;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.AssignFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.DoctorDto;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.andrzej_kosmowski.medical_clinic.dto.facility.FacilityDto;
import com.andrzej_kosmowski.medical_clinic.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<DoctorDto> getAll(Pageable pageable) {
        return doctorService.getAllDoctors(pageable);
    }

    @Operation(summary = "Get doctor by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctor found"),
        @ApiResponse(responseCode = "404", description = "Doctor not found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{id}")
    public DoctorDto getById(@PathVariable Long id) {
        return doctorService.getDoctorById(id);
    }

    @Operation(summary = "Get doctor facilities")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Facilities found"),
        @ApiResponse(responseCode = "404", description = "Facilities not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{id}/facilities")
    public List<FacilityDto> getFacilities(@PathVariable Long id) {
        return doctorService.getFacilities(id);
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
    @PutMapping("/{id}")
    public DoctorDto update(@PathVariable Long id, @RequestBody UpdateDoctorCommand command) {
        return doctorService.updateDoctor(id, command);
    }

    @Operation(summary = "Delete doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Doctor deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
    }

    @Operation(summary = "Assign facility to doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Facility assigned successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor or facility not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping("/{id}/facility")
    public DoctorDto assignFacility(
            @PathVariable Long id,
            @RequestBody AssignFacilityCommand command
    ) {
        return doctorService.assignFacility(id, command);
    }

    @Operation(summary = "Remove facility from doctor")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Facility removed successfully"),
        @ApiResponse(responseCode = "404", description = "Doctor or facility not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{doctorId}/facility/{facilityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFacility(@PathVariable Long doctorId, @PathVariable Long facilityId) {
        doctorService.removeFacility(doctorId, facilityId);
    }
}
