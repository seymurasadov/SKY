package com.sky.service;

import com.sky.enums.UserAuthority;
import com.sky.model.entity.Role;
import com.sky.model.entity.User;
import com.sky.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

  private static final String EXAMPLE_EMAIL = "user@example.com";
  private static final String NON_EXISTENT_EMAIL = "nonexistent@example.com";
  private static final String EXAMPLE_PASSWORD = "nonexistent@example.com";

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CustomUserDetailsService customUserDetailsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testLoadUserByUsername_UserExists() {
    User mockUser = new User();
    mockUser.setEmail(EXAMPLE_EMAIL);
    mockUser.setPassword(EXAMPLE_PASSWORD);
    Role role = new Role();
    role.setName(UserAuthority.USER.getRole());
    mockUser.setRoles(List.of(role));

    when(userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(mockUser));

    UserDetails userDetails = customUserDetailsService.loadUserByUsername(EXAMPLE_EMAIL);

    assertNotNull(userDetails);
    assertEquals(EXAMPLE_EMAIL, userDetails.getUsername());
    assertEquals(EXAMPLE_PASSWORD, userDetails.getPassword());
    assertTrue(userDetails.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals(UserAuthority.USER.getRole())));
  }

  @Test
  void testLoadUserByUsername_UserNotFound() {
    when(userRepository.findByEmail(NON_EXISTENT_EMAIL)).thenReturn(Optional.empty());

    assertThrows(UsernameNotFoundException.class, () -> {
      customUserDetailsService.loadUserByUsername(NON_EXISTENT_EMAIL);
    });
  }

  @Test
  void testLoadUserByUsername_MultipleRoles() {
    User mockUser = new User();
    mockUser.setEmail(EXAMPLE_EMAIL);
    mockUser.setPassword(EXAMPLE_PASSWORD);
    Role role1 = new Role();
    role1.setName(UserAuthority.USER.getRole());
    Role role2 = new Role();
    role2.setName(UserAuthority.ADMIN.getRole());
    mockUser.setRoles(List.of(role1, role2));

    when(userRepository.findByEmail(EXAMPLE_EMAIL)).thenReturn(Optional.of(mockUser));

    UserDetails userDetails = customUserDetailsService.loadUserByUsername(EXAMPLE_EMAIL);

    assertNotNull(userDetails);
    assertEquals(EXAMPLE_EMAIL, userDetails.getUsername());
    assertEquals(EXAMPLE_PASSWORD, userDetails.getPassword());
    assertTrue(userDetails.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals(UserAuthority.USER.getRole())));
    assertTrue(userDetails.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals(UserAuthority.ADMIN.getRole())));
  }
}
