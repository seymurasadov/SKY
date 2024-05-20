package com.sky.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AddExternalProjectToUserRequest extends ExternalProjectRequest {

  @NotBlank
  @Size(min = 3, max = 30)
  @Override
  public String getName() {
    return super.getName();
  }
}
