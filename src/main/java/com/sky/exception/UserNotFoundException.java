package com.sky.exception;

public class UserNotFoundException extends NotFoundException {

  public UserNotFoundException(String key, String value) {
    super("User", key, value);
  }
}
