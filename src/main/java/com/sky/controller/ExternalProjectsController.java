package com.sky.controller;

import com.sky.mapper.ExternalProjectMapper;
import com.sky.model.dto.ExternalProjectDTO;
import com.sky.model.request.AddExternalProjectToUserRequest;
import com.sky.model.request.ExternalProjectResponse;
import com.sky.security.AdminPermission;
import com.sky.security.UserOrAdminPermission;
import com.sky.service.ExternalProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/external-projects")
@RequiredArgsConstructor
public class ExternalProjectsController {

  private final ExternalProjectService externalProjectService;

  private final ExternalProjectMapper externalProjectMapper;

  @PostMapping
  @AdminPermission
  public ResponseEntity<ExternalProjectResponse> addExternalProjectToUser(@PathVariable Long userId,
                                                                          @RequestBody @Valid AddExternalProjectToUserRequest addExternalProjectToUserRequest) {
    ExternalProjectDTO externalProjectDTO = externalProjectMapper.toDto(addExternalProjectToUserRequest);

    externalProjectDTO = externalProjectService.addExternalProjectToUser(userId, externalProjectDTO);

    ExternalProjectResponse externalProjectResponse = externalProjectMapper.toResponse(externalProjectDTO);
    return new ResponseEntity<>(externalProjectResponse, HttpStatus.CREATED);
  }

  @GetMapping
  @UserOrAdminPermission
  public ResponseEntity<List<ExternalProjectResponse>> getExternalProjectsByUserId(@PathVariable Long userId) {
    List<ExternalProjectDTO> externalProjectDTOS = externalProjectService.getExternalProjectsByUserId(userId);
    List<ExternalProjectResponse> externalProjectResponses = externalProjectDTOS.stream()
      .map(externalProjectMapper::toResponse)
      .toList();
    return new ResponseEntity<>(externalProjectResponses, HttpStatus.OK);
  }
}
