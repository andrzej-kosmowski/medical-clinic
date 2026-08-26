package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.DoctorDto;
import com.andrzej_kosmowski.medical_clinic.dto.facility.CreateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.dto.facility.FacilityDto;
import com.andrzej_kosmowski.medical_clinic.dto.facility.UpdateFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.service.FacilityService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FacilityControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    FacilityService facilityService;

    @Test
    void getAll_FacilitiesExist_Response200() throws Exception {
        // given
        FacilityDto facility = new FacilityDto(1L, "Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        PageResponse<FacilityDto> response = new PageResponse<>(List.of(facility), 0, 10, 1, 1);
        when(facilityService.getAllFacilities(any(Pageable.class))).thenReturn(response);
        // when & then
        mockMvc.perform(get("/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Medical Center"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getFacilityById_FacilityExists_Response200() throws Exception {
        // given
        FacilityDto facility = new FacilityDto(1L, "Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        when(facilityService.getFacilityById(1L)).thenReturn(facility);
        // when & then
        mockMvc.perform(get("/facilities/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Warsaw"))
                .andExpect(jsonPath("$.street").value("Main Street"));
    }

    @Test
    void getDoctors_FacilityExists_Response200() throws Exception {
        // given
        DoctorDto doctor = new DoctorDto(1L, "doctor@test.pl", "Jan", "Kowalski",
                "Cardiologist", Set.of());
        when(facilityService.getDoctors(1L)).thenReturn(List.of(doctor));
        // when & then
        mockMvc.perform(get("/facilities/1/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("doctor@test.pl"))
                .andExpect(jsonPath("$[0].specialization").value("Cardiologist"));
    }

    @Test
    void create_ValidCommand_Response201() throws Exception {
        // given
        CreateFacilityCommand command = new CreateFacilityCommand("Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        FacilityDto dto = new FacilityDto(1L, "Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        when(facilityService.addFacility(command)).thenReturn(dto);
        // when & then
        mockMvc.perform(post("/facilities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Medical Center"))
                .andExpect(jsonPath("$.zipCode").value("00-001"));
        verify(facilityService).addFacility(command);
    }

    @Test
    void update_ValidCommand_Response200() throws Exception {
        // given
        UpdateFacilityCommand command = new UpdateFacilityCommand("New Clinic", "Cracow", "30-001",
                "Long Street", "20A");
        FacilityDto dto = new FacilityDto(1L, "New Clinic", "Cracow", "30-001",
                "Long Street", "20A");
        when(facilityService.updateFacility((1L), command)).thenReturn(dto);
        // when & then
        mockMvc.perform(put("/facilities/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Clinic"))
                .andExpect(jsonPath("$.city").value("Cracow"));
    }

    @Test
    void delete_FacilityExists_Response204() throws Exception {
        // when & then
        mockMvc.perform(delete("/facilities/1"))
                .andExpect(status().isNoContent());
        verify(facilityService).deleteFacility(1L);
    }
}