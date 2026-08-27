package com.andrzej_kosmowski.medical_clinic.controller;

import com.andrzej_kosmowski.medical_clinic.dto.PageResponse;
import com.andrzej_kosmowski.medical_clinic.dto.user.ChangePasswordCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.CreateUserCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.UpdateUserCommand;
import com.andrzej_kosmowski.medical_clinic.dto.user.UserDto;
import com.andrzej_kosmowski.medical_clinic.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockitoBean
    UserService userService;

    @Test
    void getAll_UsersExist_Response200() throws Exception {
        // given
        UserDto user = new UserDto(1L, "Jan", "Kowalski", "jan@test.pl");
        PageResponse<UserDto> response = new PageResponse<>(List.of(user), 0, 10, 1, 1);
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(response);
        // when & then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].email").value("jan@test.pl"))
                .andExpect(jsonPath("$.content[0].firstName").value("Jan"))
                .andExpect(jsonPath("$.content[0].lastName").value("Kowalski"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getById_UserExists_Response200() throws Exception {
        // given
        UserDto user = new UserDto(1L, "Jan", "Kowalski", "jan@test.pl");
        when(userService.getUserById(1L)).thenReturn(user);
        // when & then
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("jan@test.pl"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"));
    }

    @Test
    void create_ValidCommand_Response201() throws Exception {
        // given
        CreateUserCommand command = new CreateUserCommand("jan@test.pl", "pass123", "Jan",
                "Kowalski");
        UserDto dto = new UserDto(1L, "Jan", "Kowalski", "jan@test.pl");
        when(userService.addUser(command)).thenReturn(dto);
        // when & then
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("jan@test.pl"))
                .andExpect(jsonPath("$.firstName").value("Jan"))
                .andExpect(jsonPath("$.lastName").value("Kowalski"));
        verify(userService).addUser(command);
    }

    @Test
    void update_ValidCommand_Response200() throws Exception {
        // given
        UpdateUserCommand command = new UpdateUserCommand("new@test.pl", "Adam", "Nowak");
        UserDto dto = new UserDto(1L, "Adam", "Nowak", "new@test.pl");
        when(userService.updateUser(1L, command)).thenReturn(dto);
        // when & then
        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.firstName").value("Adam"))
                .andExpect(jsonPath("$.lastName").value("Nowak"))
                .andExpect(jsonPath("$.email").value("new@test.pl"));
        verify(userService).updateUser(1L, command);
    }

    @Test
    void delete_UserExists_Response204() throws Exception {
        // when & then
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
        verify(userService).deleteUser(1L);
    }

    @Test
    void changePassword_ValidCommand_Response204() throws Exception {
        // given
        ChangePasswordCommand command = new ChangePasswordCommand("newPass123");
        // when & then
        mockMvc.perform(patch("/users/1/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isNoContent());
        verify(userService).changePassword(1L, command);
    }
}