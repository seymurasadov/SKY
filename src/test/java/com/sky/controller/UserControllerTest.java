package com.sky.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.exception.UserNotFoundException;
import com.sky.mapper.UserMapper;
import com.sky.model.dto.UserDTO;
import com.sky.model.request.CreateUserRequest;
import com.sky.model.request.UpdateUserRequest;
import com.sky.model.request.UserResponse;
import com.sky.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerTest {

  private static final String EXAMPLE_EMAIL = "user@example.com";
  private static final String EXAMPLE_NAME = "John Doe";
  private static final String USERS_ENDPOINT = "/users";
  private static final String USERS_ENDPOINT_WITH_ID_1 = "/users/1";

  private MockMvc mockMvc;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @MockBean
  private UserService userService;

  @MockBean
  private UserMapper userMapper;

  @Autowired
  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testCreateUser() throws Exception {
    CreateUserRequest request = new CreateUserRequest();
    request.setEmail(EXAMPLE_EMAIL);
    request.setName(EXAMPLE_NAME);

    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(EXAMPLE_EMAIL);
    userDTO.setName(EXAMPLE_NAME);

    UserResponse response = new UserResponse();
    response.setEmail(EXAMPLE_EMAIL);
    response.setName(EXAMPLE_NAME);

    when(userMapper.toDto(any(CreateUserRequest.class))).thenReturn(userDTO);
    when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);
    when(userMapper.toResponse(any(UserDTO.class))).thenReturn(response);

    mockMvc.perform(post(USERS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.email").value(EXAMPLE_EMAIL))
      .andExpect(jsonPath("$.name").value(EXAMPLE_NAME));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testCreateUserWithoutAdminRole() throws Exception {
    CreateUserRequest createUserRequest = new CreateUserRequest();
    createUserRequest.setEmail(EXAMPLE_EMAIL);
    createUserRequest.setName(EXAMPLE_NAME);

    mockMvc.perform(MockMvcRequestBuilders.post(USERS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(createUserRequest)))
      .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testFindUsers() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(EXAMPLE_EMAIL);
    userDTO.setName(EXAMPLE_NAME);
    List<UserDTO> userDTOList = Collections.singletonList(userDTO);

    UserResponse response = new UserResponse();
    response.setEmail(EXAMPLE_EMAIL);
    response.setName(EXAMPLE_NAME);

    when(userService.findUsers()).thenReturn(userDTOList);
    when(userMapper.toResponse(any(UserDTO.class))).thenReturn(response);

    mockMvc.perform(get(USERS_ENDPOINT))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].email").value(EXAMPLE_EMAIL))
      .andExpect(jsonPath("$[0].name").value(EXAMPLE_NAME));
  }

  @Test
  @WithMockUser(roles = "USER")
  void testGetUser() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(EXAMPLE_EMAIL);
    userDTO.setName(EXAMPLE_NAME);

    UserResponse response = new UserResponse();
    response.setEmail(EXAMPLE_EMAIL);
    response.setName(EXAMPLE_NAME);

    when(userService.getUserById(any(Long.class))).thenReturn(userDTO);
    when(userMapper.toResponse(any(UserDTO.class))).thenReturn(response);

    mockMvc.perform(get(USERS_ENDPOINT_WITH_ID_1))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value(EXAMPLE_EMAIL))
      .andExpect(jsonPath("$.name").value(EXAMPLE_NAME));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testDeleteUser() throws Exception {
    mockMvc.perform(delete(USERS_ENDPOINT_WITH_ID_1))
      .andExpect(status().isNoContent());
  }

  @Test
  @WithMockUser(roles = "USER")
  void testUpdateUser() throws Exception {
    UpdateUserRequest request = new UpdateUserRequest();
    request.setEmail(EXAMPLE_EMAIL);
    request.setName(EXAMPLE_NAME);

    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(EXAMPLE_EMAIL);
    userDTO.setName(EXAMPLE_NAME);

    UserResponse response = new UserResponse();
    response.setEmail(EXAMPLE_EMAIL);
    response.setName(EXAMPLE_NAME);

    when(userMapper.toDto(any(UpdateUserRequest.class))).thenReturn(userDTO);
    when(userService.updateUser(any(Long.class), any(UserDTO.class))).thenReturn(userDTO);
    when(userMapper.toResponse(any(UserDTO.class))).thenReturn(response);

    mockMvc.perform(put(USERS_ENDPOINT_WITH_ID_1)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.email").value(EXAMPLE_EMAIL))
      .andExpect(jsonPath("$.name").value(EXAMPLE_NAME));
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void testCreateUserWithInvalidData() throws Exception {
    CreateUserRequest request = new CreateUserRequest();
    request.setEmail(""); // Invalid email
    request.setName(EXAMPLE_NAME);

    mockMvc.perform(post(USERS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Validation failed"))
      .andExpect(jsonPath("$.details").isNotEmpty());
  }

  @Test
  @WithMockUser(roles = "USER")
  void testGetUserNotFound() throws Exception {
    when(userService.getUserById(any(Long.class))).thenThrow(new UserNotFoundException("id", "1"));

    mockMvc.perform(get(USERS_ENDPOINT_WITH_ID_1))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.message").value("User not found with id: 1"))
      .andExpect(jsonPath("$.details").isNotEmpty());
  }

  @Test
  void testCreateUserWithoutAuth() throws Exception {
    CreateUserRequest request = new CreateUserRequest();
    request.setEmail(EXAMPLE_EMAIL);
    request.setName(EXAMPLE_NAME);

    mockMvc.perform(post(USERS_ENDPOINT)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void testFindUsersWithoutAuth() throws Exception {
    mockMvc.perform(get(USERS_ENDPOINT))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void testGetUserWithoutAuth() throws Exception {
    mockMvc.perform(get(USERS_ENDPOINT_WITH_ID_1))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void testDeleteUserWithoutAuth() throws Exception {
    mockMvc.perform(delete(USERS_ENDPOINT_WITH_ID_1))
      .andExpect(status().isUnauthorized());
  }

  @Test
  void testUpdateUserWithoutAuth() throws Exception {
    UpdateUserRequest request = new UpdateUserRequest();
    request.setEmail(EXAMPLE_EMAIL);
    request.setName(EXAMPLE_NAME);

    mockMvc.perform(put(USERS_ENDPOINT_WITH_ID_1)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isUnauthorized());
  }
}
