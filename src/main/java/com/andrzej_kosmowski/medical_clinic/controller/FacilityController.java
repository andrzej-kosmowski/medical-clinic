package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.*;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.DoctorDto;
import com.andrzej_kosmowski.medical_clinic.dto.facility.CreateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.dto.facility.FacilityDto;
import com.andrzej_kosmowski.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.service.FacilityService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/facilities", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Facilities", description = "Facility management endpoints")
public class FacilityController {
    private final FacilityService facilityService;

    @Operation(summary = "Get all facilities")
    @ApiResponse(responseCode = "200", description = "List of facilities returned successfully")
    @GetMapping
    public PageResponse<FacilityDto> getAll(Pageable pageable) {
        return facilityService.getAllFacilities(pageable);
    }

    @Operation(summary = "Get facility by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Facility found"),
        @ApiResponse(responseCode = "404", description = "Facility not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{id}")
    public FacilityDto getById(@PathVariable Long id) {
        return facilityService.getFacilityById(id);
    }

    @Operation(summary = "Get doctors assigned to facility")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Doctors found"),
        @ApiResponse(responseCode = "404", description = "Facility not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{id}/doctors")
    public List<DoctorDto> getDoctors(@PathVariable Long id) {
        return facilityService.getDoctors(id);
    }

    @Operation(summary = "Create new facility")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Facility created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid facility data",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "409", description = "Facility already exists",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacilityDto create(@RequestBody CreateFacilityCommand command) {
        log.info("Creating facility={}", command.name());
        FacilityDto facility = facilityService.addFacility(command);
        log.info("Created facility={} with id={}", facility.name(), facility.id());
        return facility;
    }

    @Operation(summary = "Update facility")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Facility updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid facility data",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
        @ApiResponse(responseCode = "404", description = "Facility not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PutMapping("/{id}")
    public FacilityDto update(@PathVariable Long id, @RequestBody UpdateFacilityCommand command) {
        log.info("Updating facility={} with id={}", command.name(), id);
        FacilityDto facility = facilityService.updateFacility(id, command);
        log.info("Updated facility={} with id={}", facility.name(), id);
        return facility;
    }

    @Operation(summary = "Delete facility")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Facility deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Facility not found",
            content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Deleting facility={}", id);
        facilityService.deleteFacility(id);
        log.info("Deleted facility={}", id);
    }
}
