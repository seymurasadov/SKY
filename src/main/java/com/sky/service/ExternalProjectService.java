package com.sky.service;

import com.sky.exception.ExternalProjectAlreadyExistsException;
import com.sky.exception.UserNotFoundException;
import com.sky.mapper.ExternalProjectMapper;
import com.sky.model.dto.ExternalProjectDTO;
import com.sky.model.entity.ExternalProject;
import com.sky.model.entity.User;
import com.sky.repository.ExternalProjectRepository;
import com.sky.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExternalProjectService {

  private final ExternalProjectRepository externalProjectRepository;

  private final ExternalProjectMapper externalProjectMapper;

  private final UserRepository userRepository;

  public ExternalProjectDTO addExternalProjectToUser(Long userId, ExternalProjectDTO externalProjectDTO) {
    ExternalProject externalProject = externalProjectMapper.toEntity(externalProjectDTO);

    User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("userId", userId.toString()));

    if (externalProjectRepository.findByNameAndUserId(externalProject.getName(), user.getId()).isPresent()) {
      throw new ExternalProjectAlreadyExistsException("name,userId", externalProject.getName() + "," + user.getId());
    }

    externalProject.setUser(user);
    externalProject = externalProjectRepository.save(externalProject);

    return externalProjectMapper.toDto(externalProject);
  }

  public List<ExternalProjectDTO> getExternalProjectsByUserId(Long userId) {
    List<ExternalProject> projects = externalProjectRepository.findByUserId(userId);
    return projects.stream()
      .map(externalProjectMapper::toDto)
      .toList();
  }
}
