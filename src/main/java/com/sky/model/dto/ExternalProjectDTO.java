package com.sky.model.dto;

import lombok.Data;

@Data
public class ExternalProjectDTO {

  private Long id;
  private String name;
  private UserDTO user;

}
