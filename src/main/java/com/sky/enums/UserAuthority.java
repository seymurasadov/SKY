package com.sky.enums;

import lombok.Getter;

@Getter
public enum UserAuthority {

  ADMIN("ROLE_ADMIN"),
  USER("ROLE_USER");

  private final String role;

  UserAuthority(String role) {
    this.role = role;
  }
}
