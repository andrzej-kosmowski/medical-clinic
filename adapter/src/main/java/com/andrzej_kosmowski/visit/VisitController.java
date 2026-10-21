package com.andrzej_kosmowski.visit;

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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/visits", produces = "application/json")
@RequiredArgsConstructor
@Tag(name = "Visits", description = "Visit scheduling endpoints")
public class VisitController {
    private final VisitService visitService;
    private final VisitMapper visitMapper;

    @Operation(summary = "Get all visits")
    @ApiResponse(responseCode = "200", description = "List of visits returned successfully")
    @GetMapping
    public PageResult<VisitDto> getAll(Pageable pageable) {
        PageQuery query = new PageQuery(pageable.getPageNumber(), pageable.getPageSize());
        return visitService.getAll(query).map(visitMapper::toDto);
    }

    @Operation(summary = "Get visit by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visit found"),
        @ApiResponse(responseCode = "404", description = "Visit not found",
                content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/{id}")
    public VisitDto getById(@PathVariable long id) {
        return visitMapper.toDto(visitService.getById(id));
    }

    @Operation(summary = "Get patient visits")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Patient visits returned successfully"),
            @ApiResponse(responseCode = "404", description = "Patient not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/patient/{patientId}")
    public List<VisitDto> getPatientVisits(@PathVariable long patientId) {
        return visitService.getPatientVisits(patientId).stream()
                .map(visitMapper::toDto)
                .toList();
    }

    @Operation(summary = "Get doctor visits")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Doctor visits returned successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/doctor/{doctorId}")
    public List<VisitDto> getDoctorVisits(@PathVariable long doctorId) {
        return visitService.getDoctorVisits(doctorId).stream()
                .map(visitMapper::toDto)
                .toList();
    }

    @Operation(summary = "Get available visits for doctor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available visits returned successfully"),
            @ApiResponse(responseCode = "404", description = "Doctor not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class)))
    })
    @GetMapping("/doctor/{doctorId}/available")
    public List<VisitDto> getAvailableDoctorVisits(@PathVariable long doctorId) {
        return visitService.getAvailableDoctorVisits(doctorId).stream()
                .map(visitMapper::toDto)
                .toList();
    }

    @Operation(summary = "Get filtered visits")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Available visits returned successfully")
    })
    @GetMapping("/search")
    public List<VisitDto> getAvailableVisitsBySpecialization(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false, defaultValue = "false") boolean availableOnly) {
        VisitSearchCriteria criteria = new VisitSearchCriteria(specialization, date, from, to, availableOnly);
        return visitService.search(criteria).stream()
                .map(visitMapper::toDto)
                .toList();
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
        log.info("Creating visit");
        Visit visit = visitService.create(command);
        VisitDto response = visitMapper.toDto(visit);
        log.info("Visit created successfully with id={}", response.id());
        return response;
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
        log.info("Assigning patient to visit");
        Visit visit = visitService.assignPatient(visitId, patientId);
        VisitDto response = visitMapper.toDto(visit);
        log.info("Visit={} assigned successfully to patient={}", visit.getId(), response.patientId());
        return response;
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
        log.info("Canceling visit {}", visitId);
        visitService.cancel(visitId);
        log.info("Visit cancelled successfully");
    }

    @Operation(summary = "Doctor deletes an unbooked visit slot")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Visit deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Visit not found",
                    content = @Content(schema = @Schema(implementation = ErrorMessageDto.class))),
    })
    @DeleteMapping("/{visitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long visitId) {
        log.info("Deleting visit {}", visitId);
        visitService.delete(visitId);
        log.info("Visit deleted successfully");
    }
}
