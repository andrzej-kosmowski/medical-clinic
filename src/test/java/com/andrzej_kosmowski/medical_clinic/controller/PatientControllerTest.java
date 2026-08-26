package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.patient.CreatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.patient.PatientDto;
import com.andrzej_kosmowski.medical_clinic.dto.patient.UpdatePatientCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.ChangePasswordCommand;
import com.andrzej_kosmowski.medical_clinic.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    PatientService patientService;

    @Test
    void getAll_patientsExists_Response200() throws Exception {
        // given
        PatientDto patient = new PatientDto("test@test.pl", "ABC123", "Jan", "Nowak",
                "111222333", LocalDate.of(1990,1,1));
        PageResponse<PatientDto> response = new PageResponse<>(List.of(patient),
                0, 10, 1 ,1);
        when(patientService.getAllPatients(any(Pageable.class))).thenReturn(response);
        // when & then
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].email").value("test@test.pl"))
                .andExpect(jsonPath("$.content[0].idCardNo").value("ABC123"))
                .andExpect(jsonPath("$.content[0].firstName").value("Jan"))
                .andExpect(jsonPath("$.content[0].lastName").value("Nowak"));
    }

    @Test
    void getPatientById_patientExists_Response200() throws Exception {
        // given
        PatientDto patient = new PatientDto("test@test.pl", "ABC123", "Jan", "Nowak",
                "111222333", LocalDate.of(1990,1,1));
        when(patientService.getPatientById(1L)).thenReturn(patient);
        // when & then
        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.pl"))
                .andExpect(jsonPath("$.idCardNo").value("ABC123"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Nowak"));
    }

    @Test
    void addPatient_ValidCommand_Response201() throws Exception {
        // given
        CreatePatientCommand command = new CreatePatientCommand("test@test.pl", "pass123", "Jan",
                "Kowalski", "ABC123", "333444555", LocalDate.of(1990,1,1));
        PatientDto patient = new PatientDto("test@test.pl", "ABC123", "Jan", "Nowak",
                "111222333", LocalDate.of(1990,1,1));
        when(patientService.addPatient(any())).thenReturn(patient);
        // when & then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@test.pl"))
                .andExpect(jsonPath("$.idCardNo").value("ABC123"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Nowak"));
    }

    @Test
    void deletePatient_PatientExists_Response204() throws Exception {
        // when & then
        mockMvc.perform(delete("/patients/1"))
                .andExpect(status().isNoContent());
        verify(patientService).deletePatient(1L);
    }

    @Test
    void updatePatient_ValidCommand_Response200() throws Exception {
        // given
        UpdatePatientCommand command = new UpdatePatientCommand("test@test.pl", "ZXC321", "Adam",
                "Nowak", "444555666", LocalDate.of(1995,5,5));
        PatientDto dto = new PatientDto("new@test.pl", "ABC123", "Adam", "Nowak",
                "111222333", LocalDate.of(1995,5,5));
        when(patientService.updatePatient(1L, command)).thenReturn(dto);
        // when & then
        mockMvc.perform(put("/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@test.pl"))
                .andExpect(jsonPath("$.idCardNo").value("ABC123"))
                .andExpect(jsonPath("$.firstName").value("Adam"))
                .andExpect(jsonPath("$.lastName").value("Nowak"))
                .andExpect(jsonPath("$.phoneNumber").value("111222333"));
    }

    @Test
    void changePatientPassword_ValidCommand_Response204() throws Exception {
        // given
        ChangePasswordCommand command = new ChangePasswordCommand("newPass123");
        // when & then
        mockMvc.perform(patch("/patients/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());
        verify(patientService).changePassword(1L, command);
    }
}