package com.sky.model.request;

import lombok.Data;

@Data
public class ExternalProjectResponse {

  private Long id;
  private String name;
  private UserResponse user;
}
