package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.visit.CreateVisitCommand;
import com.andrzej_kosmowski.medical_clinic.dto.visit.VisitDto;
import com.andrzej_kosmowski.medical_clinic.service.VisitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VisitControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    VisitService visitService;

    @Test
    void getAllVisits_VisitsExist_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(1L, LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30), 1L, null);
        PageResponse<VisitDto> response = new PageResponse<>(List.of(visit), 0, 10, 1, 1);
        when(visitService.getAllVisits(any(Pageable.class))).thenReturn(response);
        // when & then
        mockMvc.perform(get("/visits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].doctorId").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getVisitById_VisitExists_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(1L, LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30), 1L, null);
        when(visitService.getVisitById(1L)).thenReturn(visit);
        // when & then
        mockMvc.perform(get("/visits/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.doctorId").value(1));
    }

    @Test
    void getAvailableVisits_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(1L, LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30), 1L, null);
        when(visitService.getAvailableVisits()).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getPatientVisits_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(1L, LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30), 1L, 5L);
        when(visitService.getPatientVisits(5L)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/patient/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patientId").value(5));
    }

    @Test
    void getDoctorVisits_Response200() throws Exception {
        // given
        VisitDto visit = new VisitDto(1L, LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30), 2L, null);
        when(visitService.getDoctorVisits(2L)).thenReturn(List.of(visit));
        // when & then
        mockMvc.perform(get("/visits/doctor/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].doctorId").value(2));
    }

    @Test
    void create_ValidCommand_Response201() throws Exception {
        // given
        CreateVisitCommand command = new CreateVisitCommand(1L, LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30));
        VisitDto visit = new VisitDto(1L, command.startTime(), command.endTime(), 1L, null);
        when(visitService.createVisit(command)).thenReturn(visit);
        // when & then
        mockMvc.perform(post("/visits")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
        verify(visitService).createVisit(command);
    }

    @Test
    void assignPatient_Returns200() throws Exception {
        // given
        VisitDto visit = new VisitDto(1L, LocalDateTime.of(2030, 1, 1, 10, 0),
                LocalDateTime.of(2030, 1, 1, 10, 30), 1L, 5L);
        when(visitService.assignPatient(1L, 5L)).thenReturn(visit);
        // when & then
        mockMvc.perform(patch("/visits/1/patient/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(5));
        verify(visitService).assignPatient(1L, 5L);
    }

    @Test
    void cancel_Response204() throws Exception {
        // when & then
        mockMvc.perform(patch("/visits/1/cancel"))
                .andExpect(status().isNoContent());
        verify(visitService).cancelVisit(1L);
    }

    @Test
    void delete_Response204() throws Exception {
        // when & then
        mockMvc.perform(delete("/visits/1"))
                .andExpect(status().isNoContent());
        verify(visitService).deleteVisit(1L);
    }
}