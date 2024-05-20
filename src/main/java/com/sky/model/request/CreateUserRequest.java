package com.sky.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUserRequest extends UserRequest {

  @NotBlank
  @Email
  @Override
  public String getEmail() {
    return super.getEmail();
  }

  @NotBlank
  @Size(min = 3, max = 30)
  @Override
  public String getName() {
    return super.getName();
  }
}
