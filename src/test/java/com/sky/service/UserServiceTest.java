package com.sky.service;

import com.sky.enums.UserAuthority;
import com.sky.exception.UserAlreadyExistsException;
import com.sky.exception.UserNotFoundException;
import com.sky.mapper.UserMapper;
import com.sky.model.dto.UserDTO;
import com.sky.model.entity.Role;
import com.sky.model.entity.User;
import com.sky.repository.RoleRepository;
import com.sky.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

  private static final String EXISTING_EMAIL = "existing@example.com";
  private static final String EXAMPLE_EMAIL = "user@example.com";

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateUser_UserAlreadyExists() {
    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(EXISTING_EMAIL);

    when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(new User()));

    assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDTO));

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testCreateUser_Success() {

    String email = "newuser@example.com";

    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(email);

    User user = new User();
    user.setEmail(email);

    Role role = new Role();
    role.setName(UserAuthority.USER.getRole());

    when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
    when(roleRepository.findByName(UserAuthority.USER.getRole())).thenReturn(Optional.of(role));
    when(userMapper.toEntity(userDTO)).thenReturn(user);
    when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(any(User.class))).thenReturn(userDTO);

    UserDTO createdUser = userService.createUser(userDTO);

    assertNotNull(createdUser);
    assertEquals(userDTO.getEmail(), createdUser.getEmail());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void testGetUserById_UserNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
  }

  @Test
  void testGetUserById_Success() {
    User user = new User();
    user.setId(1L);
    user.setEmail(EXAMPLE_EMAIL);

    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(EXAMPLE_EMAIL);

    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userMapper.toDto(user)).thenReturn(userDTO);

    UserDTO foundUser = userService.getUserById(1L);

    assertNotNull(foundUser);
    assertEquals(userDTO.getEmail(), foundUser.getEmail());
  }

  @Test
  void testDeleteUser() {
    userService.deleteUser(1L);

    verify(userRepository).deleteById(1L);
  }

  @Test
  void testUpdateUser_UserNotFound() {
    UserDTO userDTO = new UserDTO();
    userDTO.setEmail(EXAMPLE_EMAIL);

    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, userDTO));

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testUpdateUser_Success() {
    User existingUser = new User();
    existingUser.setId(1L);
    existingUser.setEmail(EXISTING_EMAIL);

    UserDTO userDTO = new UserDTO();
    userDTO.setEmail("updated@example.com");
    userDTO.setPassword("newPassword");

    when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
    doNothing().when(userMapper).updateEntityFromDTO(userDTO, existingUser);
    when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedNewPassword");
    when(userRepository.save(existingUser)).thenReturn(existingUser);
    when(userMapper.toDto(existingUser)).thenReturn(userDTO);

    UserDTO updatedUser = userService.updateUser(1L, userDTO);

    assertNotNull(updatedUser);
    assertEquals(userDTO.getEmail(), updatedUser.getEmail());
    verify(userRepository).save(existingUser);
  }

  @Test
  void testFindUsers() {

    String email1 = "user1@example.com";

    User user1 = new User();
    user1.setId(1L);
    user1.setEmail(email1);

    String email2 = "user1@example.com";

    User user2 = new User();
    user2.setId(2L);
    user2.setEmail(email2);

    List<User> users = List.of(user1, user2);

    UserDTO userDTO1 = new UserDTO();
    userDTO1.setEmail(email1);

    UserDTO userDTO2 = new UserDTO();
    userDTO2.setEmail(email2);

    when(userRepository.findAll()).thenReturn(users);
    when(userMapper.toDto(user1)).thenReturn(userDTO1);
    when(userMapper.toDto(user2)).thenReturn(userDTO2);

    List<UserDTO> userDTOList = userService.findUsers();

    assertNotNull(userDTOList);
    assertEquals(2, userDTOList.size());
    assertTrue(userDTOList.contains(userDTO1));
    assertTrue(userDTOList.contains(userDTO2));
  }
}
