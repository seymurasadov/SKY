package com.sky.model.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude = "password")
public class UserDTO {
  private Long id;
  private String email;
  private String password;
  private String name;
}
