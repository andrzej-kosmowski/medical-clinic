package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.ErrorMessageDto;
import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.visit.CreateVisitCommand;
import com.andrzej_kosmowski.medical_clinic.dto.visit.VisitDto;
import com.andrzej_kosmowski.medical_clinic.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/visits", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Visits", description = "Visit scheduling endpoints")
public class VisitController {
    private final VisitService visitService;

    @Operation(summary = "Get all visits")
    @ApiResponse(responseCode = "200", description = "List of visits returned successfully")
    @GetMapping
    public PageResponse<VisitDto> getAll(Pageable pageable) {
        return visitService.getAllVisits(pageable);
    }

    @Operation(summary = "Get visit by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visit found"),
        @ApiResponse(responseCode = "404", description = "Visit not found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{id}")
    public VisitDto getById(@PathVariable long id) {
        return visitService.getVisitById(id);
    }

    @Operation(summary = "Get all available visits")
    @GetMapping("/available")
    @ApiResponse(responseCode = "200", description = "List of available visits returned successfully")
    public List<VisitDto> getAvailable() {
        return visitService.getAvailableVisits();
    }

    @Operation(summary = "Get patient visits")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient visits returned successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/patient/{patientId}")
    public List<VisitDto> getPatientVisits(@PathVariable long patientId) {
        return visitService.getPatientVisits(patientId);
    }

    @Operation(summary = "Get doctor visits")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor visits returned successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/doctor/{doctorId}")
    public List<VisitDto> getDoctorVisits(@PathVariable long doctorId) {
        return visitService.getDoctorVisits(doctorId);
    }

    @Operation(summary = "Doctor creates a new available visit slot")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Visit created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid visit data",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "409", description = "Overlapping visit for this doctor",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitDto create(@RequestBody CreateVisitCommand command) {
        return visitService.createVisit(command);
    }

    @Operation(summary = "Assign patient to visit")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient assigned to visit successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot book a visit in the past",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "404", description = "Visit or patient not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "409", description = "Visit already booked or patient has another visit",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping("/{visitId}/patient/{patientId}")
    public VisitDto assignPatient(@PathVariable Long visitId, @PathVariable Long patientId) {
        return visitService.assignPatient(visitId, patientId);
    }

    @Operation(summary = "Cancel patient visit")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Visit cancelled successfully"),
            @ApiResponse(responseCode = "404", description = "Visit not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @PatchMapping("/{visitId}/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long visitId) {
        visitService.cancelVisit(visitId);
    }

    @Operation(summary = "Doctor deletes an unbooked visit slot")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Visit deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Visit not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
            @ApiResponse(responseCode = "409", description = "Cannot delete a booked visit",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @DeleteMapping("/{visitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long visitId) {
        visitService.deleteVisit(visitId);
    }

}
