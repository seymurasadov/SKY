package com.sky.mapper;

import com.sky.model.dto.ExternalProjectDTO;
import com.sky.model.entity.ExternalProject;
import com.sky.model.request.AddExternalProjectToUserRequest;
import com.sky.model.request.ExternalProjectResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ExternalProjectMapper {

  ExternalProject toEntity(ExternalProjectDTO externalProjectDTO);

  ExternalProjectResponse toResponse(ExternalProjectDTO updatedExternalProject);

  ExternalProjectDTO toDto(AddExternalProjectToUserRequest addExternalProjectToUserRequest);

  ExternalProjectDTO toDto(ExternalProject externalProject);

}
