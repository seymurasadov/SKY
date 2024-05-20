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
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  private final RoleRepository roleRepository;

  private final UserMapper userMapper;

  private final PasswordEncoder passwordEncoder;

  @Value("${initial-user-password}")
  public String initialUserPassword;

  public UserDTO createUser(UserDTO userDTO) {
    if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
      throw new UserAlreadyExistsException("email", userDTO.getEmail());
    }
    User user = userMapper.toEntity(userDTO);
    user.setPassword(passwordEncoder.encode(initialUserPassword));

    Role role = roleRepository.findByName(UserAuthority.USER.getRole())
      .orElseThrow(() -> new IllegalStateException("Role does not exist"));
    user.setRoles(Collections.singletonList(role));
    User createdUser = userRepository.save(user);
    return userMapper.toDto(createdUser);
  }

  @SneakyThrows
  public UserDTO getUserById(Long id) {
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("id", id.toString()));
    return userMapper.toDto(user);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  @SneakyThrows
  public UserDTO updateUser(Long id, UserDTO userDTO) {
    User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("id", id.toString()));

    userMapper.updateEntityFromDTO(userDTO, user);

    if (Objects.nonNull(userDTO.getPassword())) {
      user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    }

    User updatedUser = userRepository.save(user);
    return userMapper.toDto(updatedUser);
  }

  public List<UserDTO> findUsers() {
    List<User> users = userRepository.findAll();
    return users.stream()
      .map(userMapper::toDto)
      .toList();
  }
}
