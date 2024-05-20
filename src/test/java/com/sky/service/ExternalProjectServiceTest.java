package com.sky.service;

import com.sky.exception.ExternalProjectAlreadyExistsException;
import com.sky.exception.UserNotFoundException;
import com.sky.mapper.ExternalProjectMapper;
import com.sky.model.dto.ExternalProjectDTO;
import com.sky.model.entity.ExternalProject;
import com.sky.model.entity.User;
import com.sky.repository.ExternalProjectRepository;
import com.sky.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExternalProjectServiceTest {

  @Mock
  private ExternalProjectRepository externalProjectRepository;

  @Mock
  private ExternalProjectMapper externalProjectMapper;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private ExternalProjectService externalProjectService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testAddExternalProjectToUser_UserNotFound() {
    Long userId = 1L;
    ExternalProjectDTO externalProjectDTO = new ExternalProjectDTO();

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class, () -> externalProjectService.addExternalProjectToUser(userId, externalProjectDTO));

    verify(externalProjectRepository, never()).save(any(ExternalProject.class));
  }

  @Test
  void testAddExternalProjectToUser_ProjectAlreadyExists() {

    String existingProjectName = "ExistingProject";

    Long userId = 1L;
    ExternalProjectDTO externalProjectDTO = new ExternalProjectDTO();
    externalProjectDTO.setName(existingProjectName);

    User user = new User();
    user.setId(userId);

    ExternalProject existingProject = new ExternalProject();
    existingProject.setName(existingProjectName);
    existingProject.setUser(user);

    ExternalProject projectEntity = new ExternalProject();
    projectEntity.setName(existingProjectName);
    projectEntity.setUser(user);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(externalProjectRepository.findByNameAndUserId(existingProjectName, userId)).thenReturn(Optional.of(existingProject));
    when(externalProjectMapper.toEntity(any(ExternalProjectDTO.class))).thenReturn(projectEntity);

    assertThrows(ExternalProjectAlreadyExistsException.class, () -> externalProjectService.addExternalProjectToUser(userId, externalProjectDTO));

    verify(externalProjectRepository, never()).save(any(ExternalProject.class));
  }

  @Test
  void testAddExternalProjectToUser_Success() {
    Long userId = 1L;
    String newProjectName = "NewProject";
    ExternalProjectDTO externalProjectDTO = new ExternalProjectDTO();
    externalProjectDTO.setName(newProjectName);

    User user = new User();
    user.setId(userId);

    ExternalProject externalProject = new ExternalProject();
    externalProject.setName(newProjectName);
    externalProject.setUser(user);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(externalProjectRepository.findByNameAndUserId(newProjectName, userId)).thenReturn(Optional.empty());
    when(externalProjectMapper.toEntity(externalProjectDTO)).thenReturn(externalProject);
    when(externalProjectRepository.save(any(ExternalProject.class))).thenReturn(externalProject);
    when(externalProjectMapper.toDto(any(ExternalProject.class))).thenReturn(externalProjectDTO);

    ExternalProjectDTO createdProject = externalProjectService.addExternalProjectToUser(userId, externalProjectDTO);

    assertNotNull(createdProject);
    assertEquals(externalProjectDTO.getName(), createdProject.getName());
    verify(externalProjectRepository).save(any(ExternalProject.class));
  }

  @Test
  void testGetExternalProjectsByUserId() {
    Long userId = 1L;
    User user = new User();
    user.setId(userId);

    String projectName1 = "Project1";

    ExternalProject project1 = new ExternalProject();
    project1.setName(projectName1);
    project1.setUser(user);

    String projectName2 = "Project2";

    ExternalProject project2 = new ExternalProject();
    project2.setName(projectName2);
    project2.setUser(user);

    List<ExternalProject> projects = List.of(project1, project2);

    ExternalProjectDTO projectDTO1 = new ExternalProjectDTO();
    projectDTO1.setName(projectName1);

    ExternalProjectDTO projectDTO2 = new ExternalProjectDTO();
    projectDTO2.setName(projectName2);

    when(externalProjectRepository.findByUserId(userId)).thenReturn(projects);
    when(externalProjectMapper.toDto(project1)).thenReturn(projectDTO1);
    when(externalProjectMapper.toDto(project2)).thenReturn(projectDTO2);

    List<ExternalProjectDTO> projectDTOList = externalProjectService.getExternalProjectsByUserId(userId);

    assertNotNull(projectDTOList);
    assertEquals(2, projectDTOList.size());
    assertTrue(projectDTOList.contains(projectDTO1));
    assertTrue(projectDTOList.contains(projectDTO2));
  }
}
