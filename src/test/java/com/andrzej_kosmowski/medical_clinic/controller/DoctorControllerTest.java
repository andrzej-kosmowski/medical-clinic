package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.AssignFacilityCommand;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.CreateDoctorCommand;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.DoctorDto;
import com.andrzej_kosmowski.medical_clinic.dto.doctor.UpdateDoctorCommand;
import com.andrzej_kosmowski.medical_clinic.dto.facility.FacilityDto;
import com.andrzej_kosmowski.medical_clinic.service.DoctorService;
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
class DoctorControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    DoctorService doctorService;

    @Test
    void getAll_DoctorsExist_Response200() throws Exception {
        // given
        DoctorDto doctor = new DoctorDto(1L, "doctor@test.pl", "Jan", "Kowalski",
                "Cardiologist", Set.of());
        PageResponse<DoctorDto> response = new PageResponse<>(List.of(doctor), 0, 10, 1, 1);
        when(doctorService.getAllDoctors(any(Pageable.class))).thenReturn(response);
        // when & then
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("doctor@test.pl"))
                .andExpect(jsonPath("$.content[0].specialization").value("Cardiologist"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getDoctorById_DoctorExists_Response200() throws Exception {
        // given
        DoctorDto doctor = new DoctorDto(1L, "doctor@test.pl", "Jan", "Kowalski",
                "Cardiologist", Set.of());
        when(doctorService.getDoctorById(1L)).thenReturn(doctor);
        // when & then
        mockMvc.perform(get("/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.specialization").value("Cardiologist"));
    }

    @Test
    void getFacilities_DoctorExists_Response200() throws Exception {
        // given
        FacilityDto facility = new FacilityDto(1L, "Medical Center", "Warsaw", "00-001",
                "Main Street", "10");
        when(doctorService.getFacilities(1L)).thenReturn(List.of(facility));
        // when & then
        mockMvc.perform(get("/doctors/1/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Medical Center"))
                .andExpect(jsonPath("$[0].city").value("Warsaw"));
    }

    @Test
    void create_ValidCommand_Response201() throws Exception {
        // given
        CreateDoctorCommand command = new CreateDoctorCommand("doctor@test.pl", "pass123", "Jan",
                "Kowalski", "Cardiologist", Set.of(1L));
        DoctorDto doctor = new DoctorDto(1L, "doctor@test.pl", "Jan", "Kowalski",
                "Cardiologist", Set.of());
        when(doctorService.addDoctor(command)).thenReturn(doctor);
        // when & then
        mockMvc.perform(post("/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("doctor@test.pl"))
                .andExpect(jsonPath("$.specialization").value("Cardiologist"));
        verify(doctorService).addDoctor(command);
    }

    @Test
    void update_ValidCommand_Response200() throws Exception {
        // given
        UpdateDoctorCommand command = new UpdateDoctorCommand("new@test.pl", "Adam",
                "Nowak", "Dentist");
        DoctorDto doctor = new DoctorDto(1L, "new@test.pl", "Adam", "Nowak",
                "Dentist", Set.of());
        when(doctorService.updateDoctor(1L, command)).thenReturn(doctor);
        // when & then
        mockMvc.perform(put("/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Adam"))
                .andExpect(jsonPath("$.specialization").value("Dentist"));
    }

    @Test
    void delete_DoctorExists_Response204() throws Exception {
        // when & then
        mockMvc.perform(delete("/doctors/1"))
                .andExpect(status().isNoContent());
        verify(doctorService).deleteDoctor(1L);
    }

    @Test
    void assignFacility_ValidCommand_Returns200() throws Exception {
        // given
        AssignFacilityCommand command = new AssignFacilityCommand(5L);
        DoctorDto doctor = new DoctorDto(1L, "doctor@test.pl", "Jan",
                "Kowalski", "Cardiologist", Set.of());
        when(doctorService.assignFacility(1L, command)).thenReturn(doctor);
        // when & then
        mockMvc.perform(patch("/doctors/1/facility")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
        verify(doctorService).assignFacility(1L, command);
    }

    @Test
    void removeFacility_ValidIds_Response204() throws Exception {
        // when & then
        mockMvc.perform(delete("/doctors/1/facility/5"))
                .andExpect(status().isNoContent());
        verify(doctorService).removeFacility(1L, 5L);
    }
}