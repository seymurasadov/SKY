package com.sky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.exception.ExternalProjectAlreadyExistsException;
import com.sky.mapper.ExternalProjectMapper;
import com.sky.model.dto.ExternalProjectDTO;
import com.sky.model.request.AddExternalProjectToUserRequest;
import com.sky.model.request.ExternalProjectResponse;
import com.sky.service.ExternalProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class ExternalProjectsControllerTest {

  private static final String PROJECT_A = "Project A";
  private static final String EXTERNAL_PROJECTS_ENDPOINT = "/users/1/external-projects";

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @MockBean
  private ExternalProjectService externalProjectService;

  @MockBean
  private ExternalProjectMapper externalProjectMapper;

  @Autowired
  private ObjectMapper objectMapper;


  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
      .apply(springSecurity()).build();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testAddExternalProjectToUser() throws Exception {
    AddExternalProjectToUserRequest request = new AddExternalProjectToUserRequest();
    request.setName(PROJECT_A);

    ExternalProjectDTO projectDTO = new ExternalProjectDTO();
    projectDTO.setName(PROJECT_A);

    ExternalProjectResponse response = new ExternalProjectResponse();
    response.setName(PROJECT_A);

    when(externalProjectMapper.toDto(any(AddExternalProjectToUserRequest.class))).thenReturn(projectDTO);
    when(externalProjectService.addExternalProjectToUser(any(Long.class), any(ExternalProjectDTO.class))).thenReturn(projectDTO);
    when(externalProjectMapper.toResponse(any(ExternalProjectDTO.class))).thenReturn(response);

    mockMvc.perform(post(EXTERNAL_PROJECTS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("Project A"));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testGetExternalProjectsByUserId() throws Exception {
    ExternalProjectDTO projectDTO = new ExternalProjectDTO();
    projectDTO.setName(PROJECT_A);
    List<ExternalProjectDTO> projectDTOs = Collections.singletonList(projectDTO);

    ExternalProjectResponse response = new ExternalProjectResponse();
    response.setName(PROJECT_A);

    when(externalProjectService.getExternalProjectsByUserId(any(Long.class))).thenReturn(projectDTOs);
    when(externalProjectMapper.toResponse(any(ExternalProjectDTO.class))).thenReturn(response);

    mockMvc.perform(get(EXTERNAL_PROJECTS_ENDPOINT))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].name").value("Project A"));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testAddExternalProjectToUserWithInvalidData() throws Exception {
    AddExternalProjectToUserRequest request = new AddExternalProjectToUserRequest();
    request.setName(""); // Invalid name

    mockMvc.perform(post(EXTERNAL_PROJECTS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Validation failed"))
      .andExpect(jsonPath("$.details").isNotEmpty());
  }

  @Test
  void testAddExternalProjectToUserWithoutAuth() throws Exception {
    AddExternalProjectToUserRequest request = new AddExternalProjectToUserRequest();
    request.setName(PROJECT_A);

    mockMvc.perform(post(EXTERNAL_PROJECTS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void testGetExternalProjectsByUserIdWithoutAuth() throws Exception {
    mockMvc.perform(get(EXTERNAL_PROJECTS_ENDPOINT))
      .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(roles = "USER")
  void testAddExternalProjectToUserWithoutAdminRole() throws Exception {
    AddExternalProjectToUserRequest request = new AddExternalProjectToUserRequest();
    request.setName(PROJECT_A);

    mockMvc.perform(post(EXTERNAL_PROJECTS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testAddExternalProjectToUserWhenProjectAlreadyExists() throws Exception {
    AddExternalProjectToUserRequest request = new AddExternalProjectToUserRequest();
    request.setName(PROJECT_A);

    when(externalProjectMapper.toDto(any(AddExternalProjectToUserRequest.class))).thenReturn(new ExternalProjectDTO());
    when(externalProjectService.addExternalProjectToUser(any(Long.class), any(ExternalProjectDTO.class)))
      .thenThrow(new ExternalProjectAlreadyExistsException("name", PROJECT_A));

    mockMvc.perform(post(EXTERNAL_PROJECTS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.message").value(String.format("External Project already exists with name: %s", PROJECT_A)))
      .andExpect(jsonPath("$.details").isNotEmpty());
  }
}
