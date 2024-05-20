package com.sky.mapper;

import com.sky.model.dto.UserDTO;
import com.sky.model.entity.User;
import com.sky.model.request.CreateUserRequest;
import com.sky.model.request.UpdateUserRequest;
import com.sky.model.request.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
  nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
  UserDTO toDto(User user);


  UserDTO toDto(CreateUserRequest createUserRequest);

  UserDTO toDto(UpdateUserRequest updateUserRequest);

  void updateEntityFromDTO(UserDTO dto, @MappingTarget User entity);

  User toEntity(UserDTO userDTO);

  UserResponse toResponse(UserDTO userDTO);
}
