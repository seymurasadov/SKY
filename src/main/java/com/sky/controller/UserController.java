package com.sky.controller;

import com.sky.mapper.UserMapper;
import com.sky.model.dto.UserDTO;
import com.sky.model.request.CreateUserRequest;
import com.sky.model.request.UpdateUserRequest;
import com.sky.model.request.UserResponse;
import com.sky.security.AdminPermission;
import com.sky.security.UserOrAdminPermission;
import com.sky.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  private final UserMapper userMapper;

  @PostMapping
  @AdminPermission
  public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {

    UserDTO userDTO = userMapper.toDto(createUserRequest);

    userDTO = userService.createUser(userDTO);

    UserResponse userResponse = userMapper.toResponse(userDTO);
    return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
  }

  @GetMapping
  @AdminPermission
  public ResponseEntity<List<UserResponse>> findUsers() {
    List<UserDTO> userDTOList = userService.findUsers();

    List<UserResponse> userResponseList = userDTOList.stream()
      .map(userMapper::toResponse)
      .toList();

    return new ResponseEntity<>(userResponseList, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  @UserOrAdminPermission
  public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
    UserDTO userDTO = userService.getUserById(id);
    UserResponse userResponse = userMapper.toResponse(userDTO);
    return new ResponseEntity<>(userResponse, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  @AdminPermission
  public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
    userService.deleteUser(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PutMapping("/{id}")
  @UserOrAdminPermission
  public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UpdateUserRequest updateUserRequest) {

    UserDTO userDTO = userMapper.toDto(updateUserRequest);

    UserDTO updatedUser = userService.updateUser(id, userDTO);
    UserResponse userResponse = userMapper.toResponse(updatedUser);

    return new ResponseEntity<>(userResponse, HttpStatus.OK);
  }
}
