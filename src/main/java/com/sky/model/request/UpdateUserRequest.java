package com.sky.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateUserRequest extends UserRequest {

  private String password;

  @Email
  @Override
  public String getEmail() {
    return super.getEmail();
  }


  @Size(min = 3, max = 30)
  @Override
  public String getName() {
    return super.getName();
  }
}
